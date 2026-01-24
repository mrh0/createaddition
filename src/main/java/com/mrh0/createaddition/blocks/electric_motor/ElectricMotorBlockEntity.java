package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class ElectricMotorBlockEntity extends GeneratingKineticBlockEntity implements EnergyTransferable {
	protected float motorSpeed;
	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private boolean cc_update_rpm = false;
	private float cc_new_rpm = 32.0f;

	private boolean active = false;

	public ElectricMotorBlockEntity(BlockEntityType<? extends ElectricMotorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(Config.ELECTRIC_MOTOR_CAPACITY.get(), Config.ELECTRIC_MOTOR_MAX_INPUT.get(), 0);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.getValue(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("generic.speed"), this, slot);
		generatedSpeed.between(-Config.ELECTRIC_MOTOR_RPM_RANGE.get(), Config.ELECTRIC_MOTOR_RPM_RANGE.get());
		generatedSpeed.value = 32;
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation(i));
		behaviours.add(generatedSpeed);
	}

	public static int step(ScrollValueBehaviour.StepContext context) {
		int current = context.currentValue;
		int step = 1;

		if (!context.shift) {
			int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);

			if (magnitude >= 4) step *= 4;
			if (magnitude >= 32) step *= 4;
			if (magnitude >= 128) step *= 4;
		}

		return step;
	}

	public float calculateAddedStressCapacity() {
		float capacity = Config.MAX_STRESS.get()/256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		String spacing = " ";
		tooltip.add(Component.literal(spacing).append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" " + Util.format(getEnergyConsumptionRate(generatedSpeed.getValue())) + "fe/t ")
				.withStyle(ChatFormatting.AQUA)).append(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
		return true;
	}

	// This is the callback that is called by the ScrollValueBehaviour!
	public void updateGeneratedRotation(int rpm) {
		motorSpeed = rpm;
		super.updateGeneratedRotation();
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	// This is the method that determines the absolute true output speed!
	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.ELECTRIC_MOTOR.has(getBlockState()))
			return 0;
		return convertToDirection(active ? motorSpeed : 0, getBlockState().getValue(ElectricMotorBlock.FACING));
	}

	@Override
	protected Block getStressConfigKey() {
		return AllBlocks.WATER_WHEEL.get();
	}

	@SuppressWarnings("unused")
	public InternalEnergyStorage getEnergyStorage() {
		return energy;
	}

	@Nullable
	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction side) {
		return energy;
	}

	public boolean isEnergyInput(Direction side) {
		return true;
	}

	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	@Override
	public void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		energy.read(compound);
		active = compound.getBoolean("active");
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
		compound.putBoolean("active", active);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
	}

	public static int getEnergyConsumptionRate(float rpm) {
		return Math.abs(rpm) > 0 ? (int)Math.max((double)Config.FE_RPM.get() * ((double)Math.abs(rpm) / 256d), (double)Config.ELECTRIC_MOTOR_MINIMUM_CONSUMPTION.get()) : 0;
	}

	@Override
	public void remove() {
		super.remove();
	}

	// CC
	boolean first = true;

	@Override
	public void tick() {
		super.tick();
		if(first) {
			motorSpeed = generatedSpeed.getValue();
			updateGeneratedRotation();
			first = false;
		}

		if(cc_update_rpm) {
			generatedSpeed.setValue(Math.round(cc_new_rpm));
			motorSpeed = cc_new_rpm;
			cc_update_rpm = false;
			updateGeneratedRotation();
		}

		//Old Lazy
		if(level.isClientSide()) return;
		int con = getEnergyConsumptionRate(motorSpeed);
		if(!active) {
			if(energy.getAmount() > con * 2L && !getBlockState().getValue(ElectricMotorBlock.POWERED)) {
				active = true;
				updateGeneratedRotation();
			}
		}
		else {
			long ext = energy.internalConsumeEnergy(con);
			if(ext < con || getBlockState().getValue(ElectricMotorBlock.POWERED)) {
				active = false;
				updateGeneratedRotation();
			}
		}
	}

	@Override
	public void tickAudio() {
		super.tickAudio();
		if (!active) return;
		if (Config.AUDIO_ENABLED.get()) CASoundScapes.play(CASoundScapes.AmbienceGroup.DYNAMO, worldPosition, 1);
	}


	public static float getDurationAngle(float deg, float initialProgress, float speed) {
		speed = Math.abs(speed);
		deg = Math.abs(deg);
		if(speed < 0.1f) return 0;
		double degreesPerTick = (speed * 360) / 60 / 20;
		return (float) ((1 - initialProgress) * deg / degreesPerTick + 1);
	}

	public static float getDurationDistance(float dis, float initialProgress, float speed) {
		speed = Math.abs(speed);
		dis = Math.abs(dis);
		if(speed < 0.1f) return 0;
		double metersPerTick = speed / 512;
		return (float) ((1 - initialProgress) * dis / metersPerTick);
	}

	// This is the callback used by the CC Peripheral!
	public boolean setRPM(float rpm) {
		rpm = Math.max(Math.min(rpm, Config.ELECTRIC_MOTOR_RPM_RANGE.get()), -Config.ELECTRIC_MOTOR_RPM_RANGE.get());
		cc_new_rpm = rpm;
		cc_update_rpm = true;
		return true;
	}

	public float getRPM() {
		return motorSpeed;
	}

	public int getGeneratedStress() {
		return (int) calculateAddedStressCapacity();
	}

	public int getEnergyConsumption() {
		return getEnergyConsumptionRate(motorSpeed);
	}

	@SuppressWarnings("unused")
	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}
}
