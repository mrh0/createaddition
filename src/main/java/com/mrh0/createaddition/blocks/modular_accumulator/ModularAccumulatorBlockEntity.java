package com.mrh0.createaddition.blocks.modular_accumulator;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.computercraft.ModularAccumulatorPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.debug.IDebugDrawer;
import com.mrh0.createaddition.energy.IMultiTileEnergyContainer;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class ModularAccumulatorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiTileEnergyContainer, IObserveTileEntity, IDebugDrawer, ThresholdSwitchObservable, EnergyTransferable {
	protected LazyOptional<EnergyStorage> energyCap;
	protected InternalEnergyStorage energyStorage;
	protected BlockPos controller;
	protected BlockPos lastKnownPos;
	protected boolean updateConnectivity;
	protected int width;
	protected int height;

	private static final int SYNC_RATE = 8;
	protected int syncCooldown;
	protected boolean queuedSync;

	private EnergyStorage escacheUp = null;
	private EnergyStorage escacheDown = null;

	public ModularAccumulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energyStorage = createEnergyStorage();
		energyCap = LazyOptional.of(() -> energyStorage);
		updateConnectivity = false;
		height = 1;
		width = 1;
		refreshCapability();
	}

	@Override
	public void onChunkUnloaded() {}

	protected InternalEnergyStorage createEnergyStorage() {
		return new InternalEnergyStorage(getCapacityMultiplier(), Config.ACCUMULATOR_MAX_INPUT.get(), Config.ACCUMULATOR_MAX_OUTPUT.get());
	}

	public void setCache(Direction side, EnergyStorage storage) {
		switch (side) {
			case DOWN -> escacheDown = storage;
			case UP -> escacheUp = storage;
		}
	}

	public EnergyStorage getCachedEnergy(Direction side) {
        return switch (side) {
            case DOWN -> escacheDown;
            case UP -> escacheUp;
            default -> null;
        };
    }

	public void firstTick() {
		updateCache();
	};

	public void updateCache() {
		if(level.isClientSide()) return;
		for(Direction side : Direction.values()) {
			updateCache(side);
		}
	}

	public void updateCache(Direction side) {
		// No need to update the cache if we're removed.
		if (isRemoved()) return;
		// Make sure the side we're checking is loaded.
		if (!level.isLoaded(worldPosition.relative(side))) {
			setCache(side, null);
			return;
		}

		EnergyStorage le = EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite());
		if(le == null) {
			setCache(side, null);
			return;
		}
		// Make sure that the side we're caching can actually be cached.
		if (side != Direction.UP && side != Direction.DOWN) return;
		// Make sure the side isn't already cached.
		if (le.equals(getCachedEnergy(side))) return;
		setCache(side, le);
	}

	protected void updateConnectivity() {
		updateConnectivity = false;
		if (level == null) return;
		if (level.isClientSide) return;
		if (!level.isLoaded(getBlockPos())) return;
		if (!isController()) return;
		CAConnectivityHandler.formMulti(this);
	}

	public LerpedFloat gauge = LerpedFloat.linear();

	long lastEnergy = 0;
	boolean firstTickState = true;
	@Override
	public void tick() {
		super.tick();
		if(firstTickState)
			firstTick();
		firstTickState = false;

		tickOutput();

		if (syncCooldown > 0) {
			syncCooldown--;
			if (syncCooldown == 0 && queuedSync)
				sendData();
		}

		if (lastKnownPos == null)
			lastKnownPos = getBlockPos();
		else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
			onPositionChanged();
			return;
		}

		if (updateConnectivity) updateConnectivity();

		// Tick Logic:
		if (!isController()) return;

		if(Math.abs(lastEnergy - energyStorage.getAmount()) > 256) {
			lastEnergy = energyStorage.getAmount();
			onEnergyChanged();
		}

		if (level == null) return;
		if (level.isClientSide()) {
			gauge.tickChaser();
			float current = gauge.getValue(1);
			if (current > 1 && Create.RANDOM.nextFloat() < 1 / 2f)
				gauge.setValueNoUpdate(current + Math.min(-(current - 1) * Create.RANDOM.nextFloat(), 0));
		}
	}

	public void tickOutput() {
		if(getControllerBE() == null) return;
		BlockState state = this.getBlockState();
		if(state.getValue(ModularAccumulatorBlock.TOP)) {
			tickOutputSide(Direction.UP);
		}
		if(state.getValue(ModularAccumulatorBlock.BOTTOM)) {
			tickOutputSide(Direction.DOWN);
		}
	}

	public void tickOutputSide(Direction side) {
		EnergyStorage ies = getCachedEnergy(side);
		if(ies == null)
			return;
		try (Transaction t = TransferUtil.getTransaction()) {
			EnergyStorageUtil.move(getControllerBE().energyStorage, ies, Config.ACCUMULATOR_MAX_OUTPUT.get(), t);
			t.commit();
		}
	}

	@Override
	public BlockPos getLastKnownPos() {
		return lastKnownPos;
	}

	@Override
	public boolean isController() {
		return controller == null || worldPosition.getX() == controller.getX()
			&& worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
	}

	@Override
	public void initialize() {
		super.initialize();
		sendData();
		if (level == null) return;
		if (level.isClientSide) invalidateRenderBoundingBox();
	}

	private void onPositionChanged() {
		removeController(true);
		lastKnownPos = worldPosition;
	}

	protected void onEnergyChanged() {
		if (level == null) return;
		if (!level.isLoaded(getBlockPos())) return;
		if (!hasLevel()) return;

		for (int yOffset = 0; yOffset < height; yOffset++) {
			for (int xOffset = 0; xOffset < width; xOffset++) {
				for (int zOffset = 0; zOffset < width; zOffset++) {
					BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
					if (!level.isLoaded(pos)) return;
					ModularAccumulatorBlockEntity acc = CAConnectivityHandler.partAt(getType(), level, pos);
					if (acc == null) continue;
					level.updateNeighbourForOutputSignal(pos, acc.getBlockState().getBlock());
				}
			}
		}

		if (!level.isClientSide) {
			setChanged();
			sendData();
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public ModularAccumulatorBlockEntity getControllerBE() {
		if (isController()) return this;
		if (level == null) return null;
		if (!level.isLoaded(getBlockPos())) return null;
		BlockEntity tileEntity = level.getBlockEntity(controller);
		if (tileEntity instanceof ModularAccumulatorBlockEntity)
			return (ModularAccumulatorBlockEntity) tileEntity;
		return null;
	}

	public void applySize(int blocks) {
		energyStorage.setCapacity(blocks * getCapacityMultiplier());
		long overflow = energyStorage.getAmount() - energyStorage.getCapacity();
		try (Transaction t = TransferUtil.getTransaction()) {
			if (overflow > 0)
				energyStorage.extract(overflow, t);
			t.commit();
		}
	}

	public void removeController(boolean keepEnergy) {
		if (level == null) return;
		if (level.isClientSide) return;
		if (!level.isLoaded(getBlockPos())) return;
		updateConnectivity = true;
		if (!keepEnergy)
			applySize(1);
		controller = null;
		width = 1;
		height = 1;
		//boiler.clear();
		onEnergyChanged();

		BlockState state = getBlockState();
		if (ModularAccumulatorBlock.isAccumulator(state)) {
			state = state.setValue(ModularAccumulatorBlock.BOTTOM, true);
			state = state.setValue(ModularAccumulatorBlock.TOP, true);
			getLevel().setBlock(worldPosition, state, 22);
		}

		refreshCapability();
		setChanged();
		sendData();
	}

	public void sendDataImmediately() {
		syncCooldown = 0;
		queuedSync = false;
		sendData();
	}

	@Override
	public void sendData() {
		if (syncCooldown > 0) {
			queuedSync = true;
			return;
		}
		super.sendData();
		queuedSync = false;
		syncCooldown = SYNC_RATE;
	}

	@Override
	public void setController(BlockPos controller) {
		if (level == null) return;
		if (level.isClientSide && !isVirtual()) return;
		if (controller.equals(this.controller)) return;
		this.controller = controller;
		refreshCapability();
		setChanged();
		sendData();
	}

	private void refreshCapability() {
		LazyOptional<EnergyStorage> oldCap = energyCap;
		energyCap = LazyOptional.of(this::handlerForCapability);
		oldCap.invalidate();
	}

	private InternalEnergyStorage handlerForCapability() {
		return isController() ? energyStorage
			: (getControllerBE() != null ? getControllerBE().handlerForCapability() : new InternalEnergyStorage(0, Config.ACCUMULATOR_MAX_INPUT.get(), Config.ACCUMULATOR_MAX_OUTPUT.get()));
	}

	@Override
	public BlockPos getController() {
		return isController() ? worldPosition : controller;
	}


	@Override
	protected AABB createRenderBoundingBox() {
		if (isController())
			return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
		else
			return super.createRenderBoundingBox();
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);

		BlockPos controllerBefore = controller;
		int prevSize = width;
		int prevHeight = height;

		updateConnectivity = compound.contains("Uninitialized");
		controller = null;
		lastKnownPos = null;

		if (compound.contains("LastKnownPos"))
			lastKnownPos = NbtUtils.readBlockPos(compound.getCompound("LastKnownPos"));
		if (compound.contains("Controller"))
			controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));

		if (isController()) {
			width = compound.getInt("Size");
			height = compound.getInt("Height");
			energyStorage.setCapacity(getTotalAccumulatorSize() * getCapacityMultiplier());
			energyStorage.read(compound.getCompound("EnergyContent"));
			try (Transaction t = TransferUtil.getTransaction()) {
				if (energyStorage.getSpace() < 0)
					energyStorage.extract(-energyStorage.getSpace(), t);
			}
		}


		if (!clientPacket)
			return;

		boolean changeOfController =
			controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
		if (changeOfController || prevSize != width || prevHeight != height) {
			if (hasLevel())
				level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
			if (isController())
				energyStorage.setCapacity(getCapacityMultiplier() * getTotalAccumulatorSize());
			invalidateRenderBoundingBox();
		}

		if (isController())
			gauge.chase(getFillState(), 0.125f, Chaser.EXP);
	}

	public float getFillState() {
		return (float) energyStorage.getAmount() / energyStorage.getCapacity();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		if (updateConnectivity)
			compound.putBoolean("Uninitialized", true);
		if (lastKnownPos != null)
			compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
		if (!isController())
			compound.put("Controller", NbtUtils.writeBlockPos(controller));
		if (isController()) {
			compound.put("EnergyContent", energyStorage.write(new CompoundTag()));
			// Used by contraptions.
			compound.putLong("EnergyCapacity", getTotalAccumulatorSize() * getCapacityMultiplier());
			compound.putInt("Size", width);
			compound.putInt("Height", height);
		}
		super.write(compound, clientPacket);

		if (!clientPacket)
			return;
		if (queuedSync)
			compound.putBoolean("LazySync", true);
	}

	@Nonnull
	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction side) {
		if (!energyCap.isPresent()) refreshCapability();
		return energyCap.getValueUnsafer();
	}

	@Override
	public void invalidate() {
		energyCap.invalidate();
		super.invalidate();
	}

	public int getTotalAccumulatorSize() {
		return width * width * height;
	}

	public static long getCapacityMultiplier() {
		return Config.ACCUMULATOR_CAPACITY.get();
	}

	public static int getMaxHeight() {
		return Config.ACCUMULATOR_MAX_HEIGHT.get();
	}

	@Override
	public int getMaxWidth() {
		return Config.ACCUMULATOR_MAX_WIDTH.get();
	}

	@Override
	public void preventConnectivityUpdate() {
		updateConnectivity = false;
	}

	@Override
	public void notifyMultiUpdated() {
		BlockState state = this.getBlockState();
		if (ModularAccumulatorBlock.isAccumulator(state)) { // safety
			state = state.setValue(ModularAccumulatorBlock.BOTTOM, getController().getY() == getBlockPos().getY());
			state = state.setValue(ModularAccumulatorBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
			if (level == null) return;
			level.setBlock(getBlockPos(), state, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
		}
		setChanged();
	}

	@Override
	public Direction.Axis getMainConnectionAxis() {
		return Direction.Axis.Y;
	}

	@Override
	public int getMaxLength(Direction.Axis longAxis, int width) {
		if (longAxis == Direction.Axis.Y)
			return getMaxHeight();
		return getMaxWidth();
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ModularAccumulatorBlockEntity controllerTE = getControllerBE();
		if (controllerTE == null) return false;

		ObservePacket.send(worldPosition, 0);

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.format((int)EnergyNetworkPacket.clientBuff)).append("fe").withStyle(ChatFormatting.AQUA));

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.capacity").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.format((int)controllerTE.energyStorage.getCapacity())).append("fe").withStyle(ChatFormatting.AQUA));
		return true;
	}

	public void observe() {}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		ModularAccumulatorBlockEntity controllerTE = getControllerBE();
		if (controllerTE == null) return;

		EnergyNetworkPacket.send(worldPosition, 0, controllerTE.energyStorage.getAmount(), player);
	}

	public boolean hasAccumulator() {
		return true;
	}

	public long getSize(int accumulator) {
		return getCapacityMultiplier();
	}

	public void setSize(int accumulator, int blocks) {
		applySize(blocks);
	}

	public InternalEnergyStorage getEnergy() {
		return energyStorage;
	}

	@Override
	public void drawDebug() {
		if (level == null) return;
		ModularAccumulatorBlockEntity controller = getControllerBE();
		if (controller == null) return;
		// Outline controller.
		VoxelShape shape = level.getBlockState(controller.getBlockPos()).getBlockSupportShape(level, controller.getBlockPos());
		CreateClient.OUTLINER.chaseAABB("ca_accumulator", shape.bounds().move(controller.getBlockPos())).lineWidth(0.0625F).colored(0xFF5B5B);
	}

	@Override
	public float getPercent() {
		ModularAccumulatorBlockEntity controllerTE = getControllerBE();
		if (controllerTE == null) return 0f;
		return controllerTE.getFillState() * 100f;
	}
}
