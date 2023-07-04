package com.mrh0.createaddition.blocks.electric_motor;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.compat.computercraft.ElectricMotorPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class ElectricMotorTileEntity extends GeneratingKineticBlockEntity implements EnergyTransferable {
	
	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private final LazyOptional<EnergyStorage> lazyEnergy;

	private boolean cc_update_rpm = false;
	private int cc_new_rpm = 32;
	
	/*public static final Integer
		RPM_RANGE = Config.ELECTRIC_MOTOR_RPM_RANGE.get(),
		DEFAULT_SPEED = 32,
		MIN_CONSUMPTION = Config.ELECTRIC_MOTOR_MINIMUM_CONSUMPTION.get(),
		STRESS = Config.BASELINE_STRESS.get();

	private static final Long
			MAX_IN = Config.ELECTRIC_MOTOR_MAX_INPUT.get();
	private static final Long CAPACITY = Config.ELECTRIC_MOTOR_CAPACITY.get();*/
	private boolean active = false;

	public ElectricMotorTileEntity(BlockEntityType<? extends ElectricMotorTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		long MAX_OUT = 0L;
		energy = new InternalEnergyStorage(Config.ELECTRIC_MOTOR_CAPACITY.get(), Config.ELECTRIC_MOTOR_MAX_INPUT.get(), 0);
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.getValue(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new KineticScrollValueBehaviour(Lang.translateDirect("generic.speed"), this, slot);
		generatedSpeed.between(-Config.ELECTRIC_MOTOR_RPM_RANGE.get(), Config.ELECTRIC_MOTOR_RPM_RANGE.get());
		generatedSpeed.value = 32;
		//generatedSpeed.withUnit(i -> Lang.translateDirect("generic.unit.rpm"));
		generatedSpeed.withCallback(this::updateGeneratedRotation);
		//generatedSpeed.withStepFunction(ElectricMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}
	
	public static int step(ScrollValueBehaviour.StepContext context) {
		int current = context.currentValue;
		int step = 1;

		if (!context.shift) {
			int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);

			if (magnitude >= 4)
				step *= 4;
			if (magnitude >= 32)
				step *= 4;
			if (magnitude >= 128)
				step *= 4;
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
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" " + Util.format(getEnergyConsumptionRate(generatedSpeed.getValue())) + "fe/t ")
				.withStyle(ChatFormatting.AQUA)).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
		return true;
	}
	
	public void updateGeneratedRotation(int i) {
		super.updateGeneratedRotation();
		cc_new_rpm = i;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.ELECTRIC_MOTOR.has(getBlockState()))
			return 0;
		return convertToDirection(active ? generatedSpeed.getValue() : 0, getBlockState().getValue(ElectricMotorBlock.FACING));
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
		return lazyEnergy.getValueUnsafer();
//		if(CreateAddition.CC_ACTIVE)
//			Peripherals.isPeripheral(getLevel(), getBlockPos(), side);
	}

	public boolean isEnergyInput(Direction side) {
		return true;// side != getBlockState().getValue(ElectricMotorBlock.FACING);
	}

	public boolean isEnergyOutput(Direction ignoredSide) {
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
		cc_antiSpam = 5;
		
	}
	
	public static int getEnergyConsumptionRate(int rpm) {
		return Math.abs(rpm) > 0 ? (int)Math.max((double)Config.FE_RPM.get() * ((double)Math.abs(rpm) / 256d), (double)Config.ELECTRIC_MOTOR_MINIMUM_CONSUMPTION.get()) : 0;
	}
	
	@Override
	public void remove() {
		lazyEnergy.invalidate();
		super.remove();
	}
	
	// CC
	int cc_antiSpam = 0;
	boolean first = true;
	
	@Override
	public void tick() {
		super.tick();
		if(first) {
			updateGeneratedRotation();
			first = false;
		}
		
		if(cc_update_rpm && cc_antiSpam > 0) {
			generatedSpeed.setValue(cc_new_rpm);
			cc_update_rpm = false;
			cc_antiSpam--;
			updateGeneratedRotation();
		}
		
		//Old Lazy
		assert level != null;
		if(level.isClientSide())
			return;
		int con = getEnergyConsumptionRate(generatedSpeed.getValue());
		if(!active) {
			if(energy.getAmount() > con * 2L && !getBlockState().getValue(ElectricMotorBlock.POWERED)) {
				active = true;
				updateGeneratedRotation();
			}
		}
		else {
			long ext = energy.internalConsumeEnergy(con);
			if (ext < con || getBlockState().getValue(ElectricMotorBlock.POWERED)) {
				active = false;
				updateGeneratedRotation();
			}
		}
	}



	public static int getDurationAngle(int deg, float initialProgress, float speed) {
		speed = Math.abs(speed);
		deg = Math.abs(deg);
		if(speed < 0.1f)
			return 0;
		double degreesPerTick = (speed * 360) / 60 / 20;
		return (int) ((1 - initialProgress) * deg / degreesPerTick + 1);
	}
	
	public static int getDurationDistance(int dis, float initialProgress, float speed) {
		speed = Math.abs(speed);
		dis = Math.abs(dis);
		if(speed < 0.1f)
			return 0;
		double metersPerTick = speed / 512;
		return (int) ((1 - initialProgress) * dis / metersPerTick);
	}
	
	public boolean setRPM(int rpm) {
		rpm = Math.max(Math.min(rpm, Config.ELECTRIC_MOTOR_RPM_RANGE.get()), -Config.ELECTRIC_MOTOR_RPM_RANGE.get());
		cc_new_rpm = rpm;
		cc_update_rpm = true;
		return cc_antiSpam > 0;
	}
	
	public int getRPM() {
		return cc_new_rpm;//generatedSpeed.getValue();
	}
	
	public int getGeneratedStress() {
		return (int) calculateAddedStressCapacity();
	}
	
	public int getEnergyConsumption() {
		return getEnergyConsumptionRate(generatedSpeed.getValue());
	}

	@SuppressWarnings("unused")
	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}
}
