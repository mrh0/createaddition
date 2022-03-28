package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoil;
import com.mrh0.createaddition.compat.computercraft.ElectricMotorPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.ConfigHelper.ConfigChange;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour.StepContext;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class ElectricMotorTileEntity extends GeneratingKineticTileEntity {
	
	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private LazyOptional<net.minecraftforge.energy.IEnergyStorage> lazyEnergy;
	private LazyOptional<ElectricMotorPeripheral> lazyPeripheral = null;
	
	private boolean cc_update_rpm = false;
	private int cc_new_rpm = 32;
	
	private static final Integer 
		RPM_RANGE = Config.ELECTRIC_MOTOR_RPM_RANGE.get(),
		DEFAULT_SPEED = 32,
		MAX_IN = Config.ELECTRIC_MOTOR_MAX_INPUT.get(),
		MIN_CONSUMPTION = Config.ELECTRIC_MOTOR_MINIMUM_CONSUMPTION.get(),
		MAX_OUT = 0,
		CAPACITY = Config.ELECTRIC_MOTOR_CAPACITY.get(),
		STRESS = Config.BASELINE_STRESS.get();
	
	private boolean active = false;

	public ElectricMotorTileEntity(BlockEntityType<? extends ElectricMotorTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		lazyEnergy = LazyOptional.of(() -> energy);
		if(CreateAddition.CC_ACTIVE) {
			lazyPeripheral = LazyOptional.of(() -> Peripherals.createElectricMotorPeripheral(this));
		}
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.getValue(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
		generatedSpeed.between(-RPM_RANGE, RPM_RANGE);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.scrollableValue = DEFAULT_SPEED;
		generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation(i));
		generatedSpeed.withStepFunction(ElectricMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}
	
	public static int step(StepContext context) {
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

		return (int) step;
	}
	
	public float calculateAddedStressCapacity() {
		float capacity = STRESS/256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" " + Multimeter.format(getEnergyConsumptionRate(generatedSpeed.getValue())) + "fe/t ")
				.withStyle(ChatFormatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
		added = true;
		return added;
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
		if(cap == CapabilityEnergy.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)))// && !level.isClientSide
			return lazyEnergy.cast();
		if(CreateAddition.CC_ACTIVE) {
			if(Peripherals.isPeripheral(cap))
				return lazyPeripheral.cast();
		}
		return super.getCapability(cap, side);
	}
	
	public boolean isEnergyInput(Direction side) {
		return side != getBlockState().getValue(ElectricMotorBlock.FACING);
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
		return Math.abs(rpm) > 0 ? (int)Math.max((double)Config.FE_RPM.get() * ((double)Math.abs(rpm) / 256d), (double)MIN_CONSUMPTION) : 0;
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		lazyEnergy.invalidate();
		if(lazyPeripheral != null)
			lazyPeripheral.invalidate();
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
		
		/*if (world.isRemote)
			return;
		if (currentInstructionDuration < 0)
			return;
		if (timer < currentInstructionDuration) {
			timer++;
			return;
		}*/
		
		//currentTarget = -1;
		//currentInstruct = Instruct.NONE;
		//currentInstructionDuration = -1;
		//timer = 0;
	}
	
	/*@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
		if (currentInstruct == Instruct.NONE)
			return;
		float currentSpeed = Math.abs(speed);
		if (Math.abs(previousSpeed) == currentSpeed)
			return;

		float initialProgress = timer / (float) currentInstructionDuration;
		if(currentInstruct == Instruct.ANGLE)
			currentInstructionDuration = getDurationAngle(currentTarget, initialProgress, generatedSpeed.getValue());
		timer = 0;
	}*/
	
	/*public float runAngle(int angle, int speed) {
		generatedSpeed.setValue(angle < 0 ? -speed : speed);
		currentInstructionDuration = getDurationAngle(Math.abs(angle), 0, speed);
		//currentTarget = angle;
		//timer = 0;
		
		return (float)currentInstructionDuration / 20f;
	}*/
	
	
	
	public int getDurationAngle(int deg, float initialProgress, float speed) {
		speed = Math.abs(speed);
		deg = Math.abs(deg);
		if(speed < 0.1f)
			return 0;
		double degreesPerTick = (speed * 360) / 60 / 20;
		return (int) ((1 - initialProgress) * deg / degreesPerTick + 1);
	}
	
	public int getDurationDistance(int dis, float initialProgress, float speed) {
		speed = Math.abs(speed);
		dis = Math.abs(dis);
		if(speed < 0.1f)
			return 0;
		double metersPerTick = speed / 512;
		return (int) ((1 - initialProgress) * dis / metersPerTick);
	}
	
	public boolean setRPM(int rpm) {
		//System.out.println("SETSPEED" + rpm);
		rpm = Math.max(Math.min(rpm, RPM_RANGE), -RPM_RANGE);
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
		return getBlockState().getValue(TeslaCoil.POWERED);
	}
}