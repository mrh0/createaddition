package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.compat.computercraft.ElectricMotorPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class ElectricMotorBlockEntity extends GeneratingKineticBlockEntity {

	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private LazyOptional<net.minecraftforge.energy.IEnergyStorage> lazyEnergy;
	private LazyOptional<ElectricMotorPeripheral> lazyPeripheral = null;

	private boolean cc_update_rpm = false;
	private int cc_new_rpm = 32;

	private boolean active = false;

	public ElectricMotorBlockEntity(BlockEntityType<? extends ElectricMotorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(Config.ELECTRIC_MOTOR_CAPACITY.get(), Config.ELECTRIC_MOTOR_MAX_INPUT.get(), 0);
		lazyEnergy = LazyOptional.of(() -> energy);
		if(CreateAddition.CC_ACTIVE) {
			lazyPeripheral = LazyOptional.of(() -> Peripherals.createElectricMotorPeripheral(this));
		}
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
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation(i));
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
		tooltip.add(Component.literal(spacing).append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" " + Util.format(getEnergyConsumptionRate(generatedSpeed.getValue())) + "fe/t ")
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

	public InternalEnergyStorage getEnergyStorage() {
		return energy;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == ForgeCapabilities.ENERGY)// && (isEnergyInput(side) || isEnergyOutput(side))
			return lazyEnergy.cast();
		if(CreateAddition.CC_ACTIVE) {
			if(Peripherals.isPeripheral(cap))
				return lazyPeripheral.cast();
		}
		return super.getCapability(cap, side);
	}

	public boolean isEnergyInput(Direction side) {
		return true;// side != getBlockState().getValue(ElectricMotorBlock.FACING);
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
		cc_antiSpam = 5;

	}

	public static int getEnergyConsumptionRate(int rpm) {
		return Math.abs(rpm) > 0 ? (int)Math.max((double)Config.FE_RPM.get() * ((double)Math.abs(rpm) / 256d), (double)Config.ELECTRIC_MOTOR_MINIMUM_CONSUMPTION.get()) : 0;
	}

	@Override
	public void remove() {
		lazyEnergy.invalidate();
		if(lazyPeripheral != null)
			lazyPeripheral.invalidate();
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
		if(level.isClientSide())
			return;
		int con = getEnergyConsumptionRate(generatedSpeed.getValue());
		if(!active) {
			if(energy.getEnergyStored() > con * 2 && !getBlockState().getValue(ElectricMotorBlock.POWERED)) {
				active = true;
				updateGeneratedRotation();
			}
		}
		else {
			int ext = energy.internalConsumeEnergy(con);
			if(ext < con || getBlockState().getValue(ElectricMotorBlock.POWERED)) {
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

	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}
}
