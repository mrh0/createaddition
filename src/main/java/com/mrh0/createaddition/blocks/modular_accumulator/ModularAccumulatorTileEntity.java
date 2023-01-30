package com.mrh0.createaddition.blocks.modular_accumulator;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IMultiTileEnergyContainer;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.Create;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ModularAccumulatorTileEntity extends SmartTileEntity implements IHaveGoggleInformation, IMultiTileEnergyContainer, IObserveTileEntity {

	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(),
			MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(),
			MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get(),
			MAX_HEIGHT = Config.ACCUMULATOR_MAX_HEIGHT.get(),
			MAX_WIDTH = Config.ACCUMULATOR_MAX_WIDTH.get();

	protected LazyOptional<IEnergyStorage> energyCap;
	protected InternalEnergyStorage energyStorage;
	protected BlockPos controller;
	protected BlockPos lastKnownPos;
	protected boolean updateConnectivity;
	protected int width;
	protected int height;

	private static final int SYNC_RATE = 8;
	protected int syncCooldown;
	protected boolean queuedSync;

	public ModularAccumulatorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energyStorage = createEnergyStorage();
		energyCap = LazyOptional.of(() -> energyStorage);
		updateConnectivity = false;
		height = 1;
		width = 1;
		refreshCapability();
	}

	protected InternalEnergyStorage createEnergyStorage() {
		return new InternalEnergyStorage(getCapacityMultiplier(), Config.ACCUMULATOR_MAX_INPUT.get(), Config.ACCUMULATOR_MAX_OUTPUT.get());
	}

	protected void updateConnectivity() {
		updateConnectivity = false;
		if (level.isClientSide)
			return;
		if (!isController())
			return;
		ConnectivityHandler.formMulti(this);
	}
	
	public LerpedFloat gauge = LerpedFloat.linear();

	int lastEnergy = 0;
	@Override
	public void tick() {
		super.tick();
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

		if (updateConnectivity)
			updateConnectivity();
		
		// Tick Logic:
		if (!isController()) return;
		
		if(Math.abs(lastEnergy - energyStorage.getEnergyStored()) > 256) {
			lastEnergy = energyStorage.getEnergyStored();
			onEnergyChanged();
		}
		
		if (level.isClientSide()) {
			gauge.tickChaser();
			float current = gauge.getValue(1);
			if (current > 1 && Create.RANDOM.nextFloat() < 1 / 2f)
				gauge.setValueNoUpdate(current + Math.min(-(current - 1) * Create.RANDOM.nextFloat(), 0));
			return;
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
		if (level.isClientSide)
			invalidateRenderBoundingBox();
	}

	private void onPositionChanged() {
		removeController(true);
		lastKnownPos = worldPosition;
	}

	protected void onEnergyChanged() {
		if (!hasLevel())
			return;

		for (int yOffset = 0; yOffset < height; yOffset++) {
			for (int xOffset = 0; xOffset < width; xOffset++) {
				for (int zOffset = 0; zOffset < width; zOffset++) {
					BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
					ModularAccumulatorTileEntity acc = ConnectivityHandler.partAt(getType(), level, pos);
					if (acc == null)
						continue;
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
	public ModularAccumulatorTileEntity getControllerTE() {
		if (isController())
			return this;
		BlockEntity tileEntity = level.getBlockEntity(controller);
		if (tileEntity instanceof ModularAccumulatorTileEntity)
			return (ModularAccumulatorTileEntity) tileEntity;
		return null;
	}

	public void applySize(int blocks) {
		energyStorage.setCapacity(blocks * getCapacityMultiplier());
		int overflow = energyStorage.getEnergyStored() - energyStorage.getMaxEnergyStored();
		if (overflow > 0)
			energyStorage.extractEnergy(overflow, false);
	}

	public void removeController(boolean keepEnergy) {
		if (level.isClientSide)
			return;
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
		if (level.isClientSide && !isVirtual())
			return;
		if (controller.equals(this.controller))
			return;
		this.controller = controller;
		refreshCapability();
		setChanged();
		sendData();
	}

	private void refreshCapability() {
		LazyOptional<IEnergyStorage> oldCap = energyCap;
		energyCap = LazyOptional.of(() -> handlerForCapability());
		oldCap.invalidate();
	}

	private InternalEnergyStorage handlerForCapability() {
		return isController() ? energyStorage
			: (getControllerTE() != null ? getControllerTE().handlerForCapability() : new InternalEnergyStorage(0, MAX_IN, MAX_OUT));
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

	@Nullable
	public ModularAccumulatorTileEntity getOtherTileEntity(Direction direction) {
		BlockEntity otherTE = level.getBlockEntity(worldPosition.relative(direction));
		if (otherTE instanceof ModularAccumulatorTileEntity)
			return (ModularAccumulatorTileEntity) otherTE;
		return null;
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
			if (energyStorage.getSpace() < 0)
				energyStorage.extractEnergy(-energyStorage.getSpace(), true);
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
		return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
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
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!energyCap.isPresent())
			refreshCapability();
		if (cap == ForgeCapabilities.ENERGY)
			return energyCap.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidate() {
		energyCap.invalidate();
		super.invalidate();
	}

	public int getTotalAccumulatorSize() {
		return width * width * height;
	}

	public static int getCapacityMultiplier() {
		return Config.ACCUMULATOR_CAPACITY.get();
	}

	public static int getMaxHeight() {
		return MAX_HEIGHT;
	}
	
	@Override
	public int getMaxWidth() {
		return MAX_WIDTH;
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
			level.setBlock(getBlockPos(), state, 6);
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
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ModularAccumulatorTileEntity controllerTE = getControllerTE();
		if (controllerTE == null)
			return false;
		
		ObservePacket.send(worldPosition, 0);
		
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));
		
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.capacity").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.format((int)controllerTE.energyStorage.getMaxEnergyStored())).append("fe/t").withStyle(ChatFormatting.AQUA));
		return true;
	}
	
	public void observe() {
		if(!isController()) return;
		// ObservePacket.send(worldPosition, 0);
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		ModularAccumulatorTileEntity controllerTE = getControllerTE();
		if (controllerTE == null)
			return;
		
		EnergyNetworkPacket.send(worldPosition, 0, controllerTE.energyStorage.getEnergyStored(), player);
		// causeBlockUpdate();
	}
}
