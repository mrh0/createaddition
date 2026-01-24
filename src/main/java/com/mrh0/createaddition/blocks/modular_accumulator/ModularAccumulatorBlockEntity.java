package com.mrh0.createaddition.blocks.modular_accumulator;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.debug.IDebugDrawer;
import com.mrh0.createaddition.energy.IMultiTileEnergyContainer;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.network.EnergyNetworkPacketPayload;
import com.mrh0.createaddition.network.IObserveBlockEntity;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public class ModularAccumulatorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IMultiTileEnergyContainer, IObserveBlockEntity, IDebugDrawer, ThresholdSwitchObservable {
	protected InternalEnergyStorage energyCapability;
	protected BlockPos controller;
	protected BlockPos lastKnownPos;
	protected boolean updateConnectivity;
	protected int width;
	protected int height;
	protected boolean updateCapability;

	private static final int SYNC_RATE = 8;
	protected int syncCooldown;
	protected boolean queuedSync;

	private final EnumSet<Direction> invalidSides = EnumSet.allOf(Direction.class);
	private final EnumMap<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> cache = new EnumMap<>(Direction.class);
	// protected LazyOptional<ModularAccumulatorPeripheral> peripheral;

	public ModularAccumulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energyCapability = createEnergyStorage();
		updateConnectivity = false;
		height = 1;
		width = 1;
		updateCapability = false;
		refreshCapability();

		// if (CreateAddition.CC_ACTIVE) this.peripheral = LazyOptional.of(() -> Peripherals.createModularAccumulatorPeripheral(this));
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				CABlockEntities.MODULAR_ACCUMULATOR.get(),
				(be, context) -> {
					if (be.energyCapability == null)
						be.refreshCapability();
					return be.energyCapability;
				}
		);
	}

	@Override
	public void onChunkUnloaded() {}

	protected InternalEnergyStorage createEnergyStorage() {
		return new InternalEnergyStorage(getCapacityMultiplier(), CommonConfig.ACCUMULATOR_MAX_INPUT.get(), CommonConfig.ACCUMULATOR_MAX_OUTPUT.get());
	}

	public void updateCache() {
		if (level == null) return;
		if (level.isClientSide()) return;
		for (Direction side : Direction.values()) {
			cache.put(side, BlockCapabilityCache.create(
					Capabilities.EnergyStorage.BLOCK,
					(ServerLevel) level,
					getBlockPos().relative(side),
					side.getOpposite(),
					() -> !this.isRemoved(),
					() -> invalidSides.add(side)
			));
		}
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

	int lastEnergy = 0;
	int energyChangeTick = 0;
	@Override
	public void tick() {
		super.tick();
		if(!invalidSides.isEmpty()) {
			updateCache();
			invalidSides.clear();
		}

		tickOutput();

		if (syncCooldown > 0) {
			syncCooldown--;
			if (syncCooldown == 0 && queuedSync) sendData();
		}

		if (lastKnownPos == null) lastKnownPos = getBlockPos();
		else if (!lastKnownPos.equals(worldPosition) && worldPosition != null) {
			onPositionChanged();
			return;
		}

		if (updateCapability) {
			updateCapability = false;
			refreshCapability();
		}

		if (updateConnectivity) updateConnectivity();

		// Tick Logic:
		if (!isController()) return;

		if(Math.abs(lastEnergy - energyCapability.getEnergyStored()) > 256) {
			lastEnergy = energyCapability.getEnergyStored();
			onEnergyChanged();
		}

		if (energyChangeTick > 0) energyChangeTick--;

		if (level == null) return;
		if (level.isClientSide()) {
			CatnipServices.PLATFORM.executeOnClientOnly(() -> this::tickAudio);
			gauge.tickChaser();
			float current = gauge.getValue(1);
			if (current > 1 && Create.RANDOM.nextFloat() < 1 / 2f)
				gauge.setValueNoUpdate(current + Math.min(-(current - 1) * Create.RANDOM.nextFloat(), 0));
		}
	}

	public void tickOutput() {
		if (getControllerBE() == null) return;
		BlockState state = this.getBlockState();
		if (state.getValue(ModularAccumulatorBlock.TOP)) tickOutputSide(Direction.UP);
		if (state.getValue(ModularAccumulatorBlock.BOTTOM)) tickOutputSide(Direction.DOWN);
	}

	public void tickOutputSide(Direction side) {
		if (level == null) return;
		if (!level.isLoaded(getBlockPos())) return;
		if (!level.isLoaded(getBlockPos().relative(side))) return;
		var sideCache = cache.get(side);
		if (sideCache == null) return;
		IEnergyStorage ies = sideCache.getCapability();
		if(ies == null) return;
		int ext = getControllerBE().energyCapability.extractEnergy(ies.receiveEnergy(CommonConfig.ACCUMULATOR_MAX_OUTPUT.get(), true), false);
		int rec = ies.receiveEnergy(ext, false);
	}

	public void tickAudio() {
		if (energyChangeTick == 0) return;
		int sizeInBlocks = getTotalAccumulatorSize();
		float pitch = 0.75f;
		if (sizeInBlocks < 4) pitch = 1.25f;
		if (sizeInBlocks < 9) pitch = 1f;
		CASoundScapes.play(CASoundScapes.AmbienceGroup.CHARGE, worldPosition, pitch);
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

		energyChangeTick = 20;

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
	public @Nullable ModularAccumulatorBlockEntity getControllerBE() {
		if (isController()) return this;
		if (level == null) return this;
		if (!level.isLoaded(getBlockPos())) return this;
		BlockEntity tileEntity = level.getBlockEntity(controller);
		if (tileEntity instanceof ModularAccumulatorBlockEntity)
			return (ModularAccumulatorBlockEntity) tileEntity;
		return null;
	}

	public void applySize(int blocks) {
		energyCapability.setCapacity(blocks * getCapacityMultiplier());
		int overflow = energyCapability.getEnergyStored() - energyCapability.getMaxEnergyStored();
		if (overflow > 0) energyCapability.extractEnergy(overflow, false);
	}

	public void removeController(boolean keepEnergy) {
		if (level == null) return;
		if (level.isClientSide) return;
		if (!level.isLoaded(getBlockPos())) return;
		updateConnectivity = true;
		if (!keepEnergy) applySize(1);
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

	void refreshCapability() {
		energyCapability = handlerForCapability();
		invalidateCapabilities();
	}

	private InternalEnergyStorage handlerForCapability() {
		if (isController()) return energyCapability;
		ModularAccumulatorBlockEntity controllerBE = getControllerBE();
		if (controllerBE != null && controllerBE != this) {
			return controllerBE.handlerForCapability();
		}
		return new InternalEnergyStorage(0, CommonConfig.ACCUMULATOR_MAX_INPUT.get(), CommonConfig.ACCUMULATOR_MAX_OUTPUT.get());
	}

	@Override
	public BlockPos getController() {
		return isController() ? worldPosition : controller;
	}


	@Override
	protected AABB createRenderBoundingBox() {
		if (isController()) return super.createRenderBoundingBox().expandTowards(width - 1, height - 1, width - 1);
		else return super.createRenderBoundingBox();
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);

		BlockPos controllerBefore = controller;
		int prevSize = width;
		int prevHeight = height;

		updateConnectivity = tag.contains("Uninitialized");
		controller = null;
		lastKnownPos = null;

		if (tag.contains("LastKnownPos")) lastKnownPos = NbtUtils.readBlockPos(tag, "LastKnownPos").orElse(BlockPos.ZERO);
		if (tag.contains("Controller")) controller = NbtUtils.readBlockPos(tag, "Controller").orElse(BlockPos.ZERO);

		if (isController()) {
			width = tag.getInt("Size");
			height = tag.getInt("Height");
			energyCapability.setCapacity(getTotalAccumulatorSize() * getCapacityMultiplier());
			energyCapability.read(tag.getCompound("EnergyContent"));
			if (energyCapability.getSpace() < 0) energyCapability.extractEnergy(-energyCapability.getSpace(), true);
		}

		updateCapability = true;

		if (!clientPacket) return;

		boolean changeOfController = controllerBefore == null ? controller != null : !controllerBefore.equals(controller);
		if (changeOfController || prevSize != width || prevHeight != height) {
			if (hasLevel()) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
			if (isController()) energyCapability.setCapacity(getCapacityMultiplier() * getTotalAccumulatorSize());
			invalidateRenderBoundingBox();
		}

		if (isController()) gauge.chase(getFillState(), 0.125f, LerpedFloat.Chaser.EXP);
	}

	public float getFillState() {
		return (float) energyCapability.getEnergyStored() / energyCapability.getMaxEnergyStored();
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		if (updateConnectivity) tag.putBoolean("Uninitialized", true);
		if (lastKnownPos != null) tag.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
		if (!isController()) tag.put("Controller", NbtUtils.writeBlockPos(controller));
		if (isController()) {
			tag.put("EnergyContent", energyCapability.write(new CompoundTag()));
			// Used by contraptions.
			tag.putInt("EnergyCapacity", getTotalAccumulatorSize() * getCapacityMultiplier());
			tag.putInt("Size", width);
			tag.putInt("Height", height);
		}
		super.writeSafe(tag, registries);

		if (!clientPacket) return;
		if (queuedSync) tag.putBoolean("LazySync", true);
	}

	public int getTotalAccumulatorSize() {
		return width * width * height;
	}

	public static int getCapacityMultiplier() {
		return CommonConfig.ACCUMULATOR_CAPACITY.get();
	}

	public static int getMaxHeight() {
		return CommonConfig.ACCUMULATOR_MAX_HEIGHT.get();
	}

	@Override
	public int getMaxWidth() {
		return CommonConfig.ACCUMULATOR_MAX_WIDTH.get();
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
		if (longAxis == Direction.Axis.Y) return getMaxHeight();
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

		ObservePacketPayload.send(worldPosition, 0);

		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)).forGoggles(tooltip);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" ")
				.append(Util.format((int)EnergyNetworkPacketPayload.clientBuff)).append("⚡").withStyle(ChatFormatting.AQUA)).forGoggles(tooltip);

		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.capacity").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" ")
				.append(Util.format((int)controllerTE.energyCapability.getMaxEnergyStored())).append("⚡").withStyle(ChatFormatting.AQUA)).forGoggles(tooltip);
		return true;
	}

	public void observe() {}

	@Override
	public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
		ModularAccumulatorBlockEntity controllerTE = getControllerBE();
		if (controllerTE == null) return;

		EnergyNetworkPacketPayload.send(worldPosition, 0, controllerTE.energyCapability.getEnergyStored(), player);
	}

	public boolean hasAccumulator() {
		return true;
	}

	public int getSize(int accumulator) {
		return getCapacityMultiplier();
	}

	public void setSize(int accumulator, int blocks) {
		applySize(blocks);
	}

	public InternalEnergyStorage getEnergy() {
		return energyCapability;
	}

	@Override
	public void drawDebug() {
		if (level == null) return;
		ModularAccumulatorBlockEntity controller = getControllerBE();
		if (controller == null) return;
		// Outline controller.
		VoxelShape shape = level.getBlockState(controller.getBlockPos()).getBlockSupportShape(level, controller.getBlockPos());
		Outliner.getInstance().chaseAABB("ca_accumulator", shape.bounds().move(controller.getBlockPos())).lineWidth(0.0625F).colored(0xFF5B5B);
	}

	@Override
	public int getMaxValue() {
		return 100;
	}

	@Override
	public int getMinValue() {
		return 0;
	}

	@Override
	public int getCurrentValue() {
		ModularAccumulatorBlockEntity controllerBE = getControllerBE();
		if (controllerBE == null) return 0;
		return (int)((float)controllerBE.energyCapability.getEnergyStored() / (float)controllerBE.energyCapability.getMaxEnergyStored() * 100f);
	}

	@Override
	public MutableComponent format(int i) {
		return Component.literal(i + "%");
	}
}
