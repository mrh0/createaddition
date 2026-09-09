package com.mrh0.createaddition.blocks.electric_pump;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.joml.Quaternionf;

public class ElectricPumpBlockEntity extends PumpBlockEntity {
	public static final float[][] PARTIAL_OFFSETS = {
			{8 / 16f, 4 / 16f, 8 / 16f},  // bottom face at y=2
			{8 / 16f, 12 / 16f, 8 / 16f}  // bottom face at y=10
	};

	public static final float[] PARTIAL_PHASE_OFFSETS = {6f, 0f};

	protected float pumpSpeed;
	protected ScrollValueBehaviour speedBehaviour;
	protected final InternalEnergyStorage energy;
	private final IEnergyStorage capability;

	private boolean active = false;

	public ElectricPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(CommonConfig.ELECTRIC_PUMP_CAPACITY.get(), CommonConfig.ELECTRIC_PUMP_MAX_INPUT.get(), 0);
		capability = energy;
		setLazyTickRate(20);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				CABlockEntities.ELECTRIC_PUMP.get(),
				(be, context) -> be.capability
		);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		CenteredSideValueBoxTransform slot =
				new CenteredSideValueBoxTransform((pump, side) -> side.getAxis() != pump.getValue(ElectricPumpBlock.FACING).getAxis());

		speedBehaviour = new KineticScrollValueBehaviour(CreateLang.translateDirect("generic.speed"), this, slot);
		speedBehaviour.between(32, CommonConfig.ELECTRIC_PUMP_RPM_RANGE.get());
		speedBehaviour.value = 32;
		behaviours.add(speedBehaviour);
	}

	protected void applySpeed() {
		float target = active ? pumpSpeed : 0;
		if (target == speed)
			return;
		float prevSpeed = speed;
		speed = target;
		onSpeedChanged(prevSpeed);

		sendData();
	}

	@Override
	public float getGeneratedSpeed() {
		return active ? pumpSpeed : 0;
	}

	// no kinetic network - speed is FE-driven only
	@Override
	public void attachKinetics() {
		updateSpeed = false;
	}

	@Override
	public void setSource(BlockPos source) {
	}

	@Override
	public float calculateStressApplied() {
		return 0f;
	}

	@Override
	public float calculateAddedStressCapacity() {
		return 0f;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" " + Util.format(getEnergyConsumptionRate(pumpSpeed)) + "⚡/t ")
				.withStyle(ChatFormatting.AQUA).append(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY))).forGoggles(tooltip);
		return true;
	}

	public static int getEnergyConsumptionRate(float speed) {
		if (Math.abs(speed) <= 0)
			return 0;
		return (int) Math.max(1, Math.round(CommonConfig.ELECTRIC_PUMP_FE_RPM.get() * Math.abs(speed) / 256d));
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		energy.read(tag);
		active = tag.getBoolean("Active");
	}

	// writeClient() calls write(), not writeSafe() - both need energy/active for the sync packet
	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		energy.write(tag);
		tag.putBoolean("Active", active);
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		energy.write(tag);
		tag.putBoolean("Active", active);
	}

	@Override
	public void tick() {
		super.tick();

		float behaviourSpeed = speedBehaviour.getValue();
		if (behaviourSpeed != pumpSpeed) {
			pumpSpeed = behaviourSpeed;
			applySpeed();
		}

		if (level.isClientSide())
			return;

		int con = getEnergyConsumptionRate(pumpSpeed);
		boolean powered = getBlockState().getValue(ElectricPumpBlock.POWERED);
		boolean shouldRun = !powered && con > 0 && energy.getEnergyStored() >= con;

		if (shouldRun)
			energy.internalConsumeEnergy(con);

		if (shouldRun != active) {
			active = shouldRun;
			applySpeed();
		}
	}

	public float getPumpSpeed() {
		return pumpSpeed;
	}

	public int getEnergyConsumption() {
		return getEnergyConsumptionRate(pumpSpeed);
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public void tickAudio() {
		if (!active)
			return;
		if (CommonConfig.AUDIO_ENABLED.get())
			CASoundScapes.play(CASoundScapes.AmbienceGroup.DYNAMO, worldPosition, 1);
	}

	public static float getPulseScale(boolean active, float pumpSpeed, float renderTime, float phaseOffsetTicks) {
		if (!active)
			return 1f;
		float rate = Math.abs(pumpSpeed) / 32f;
		return 1f + Mth.sin((renderTime + phaseOffsetTicks) * 0.1f * rate) * 0.1f;
	}

	public static Quaternionf getFacingRotation(Direction facing) {
		return new Quaternionf().rotateTo(0, 1, 0, facing.getStepX(), facing.getStepY(), facing.getStepZ());
	}
}