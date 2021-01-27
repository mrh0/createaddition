package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour.StepContext;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class ElectricMotorTileEntity extends GeneratingKineticTileEntity {

	
	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private LazyOptional<net.minecraftforge.energy.IEnergyStorage> lazyEnergy;
	
	private static final Integer 
		MIN_RPM = Config.ELECTRIC_MOTOR_MIN_RPM.get(),
		MAX_RPM = Config.ELECTRIC_MOTOR_MAX_RPM.get(),
		DEFAULT_SPEED = MIN_RPM,
		MAX_IN = Config.ELECTRIC_MOTOR_MAX_INPUT.get(),
		MAX_OUT = 0,
		CAPACITY = Config.ELECTRIC_MOTOR_CAPACITY.get();
	
	private boolean active = false;

	public ElectricMotorTileEntity(TileEntityType<? extends ElectricMotorTileEntity> type) {
		super(type);
		energy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.get(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
		generatedSpeed.between(MIN_RPM, MAX_RPM);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.scrollableValue = DEFAULT_SPEED;
		generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		generatedSpeed.withStepFunction(ElectricMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}
	
	public static int step(StepContext context) {
		if (context.shift)
			return 1;
		int current = context.currentValue;
		int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);
		int step = 32;
		
		if (magnitude >= 128)
			step *= 4;
		return step;
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").mergeStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").mergeStyle(TextFormatting.AQUA)));
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.consumption").mergeStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.format(getEnergyConsumptionRate(generatedSpeed.getValue()) * 20) + "fe/s ")
				.mergeStyle(TextFormatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").mergeStyle(TextFormatting.DARK_GRAY)));
		added = true;
		return added;
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
		return convertToDirection(active ? generatedSpeed.getValue() : 0, getBlockState().get(ElectricMotorBlock.FACING));
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
		if(cap == CapabilityEnergy.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)) && !world.isRemote)
			return lazyEnergy.cast();
		return super.getCapability(cap, side);
	}
	
	public boolean isEnergyInput(Direction side) {
		return side != getBlockState().get(ElectricMotorBlock.FACING);
	}

	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		energy.read(compound);
		active = compound.getBoolean("active");
	}
	
	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
		compound.putBoolean("active", active);
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		if(world.isRemote())
			return;
		int con = getEnergyConsumptionRate(generatedSpeed.getValue()) * 20;
		if(!active) {
			if(energy.getEnergyStored() > con * 2) {
				active = true;
			}
		}
		else {
			
			int ext = energy.internalConsumeEnergy(con);
			if(ext < con) {
				active = false;
			}
		}
		updateGeneratedRotation();
	}
	
	public static int getEnergyConsumptionRate(int rpm) {
		return Config.ELECTRIC_MOTOR_CONSUMPTION.get() * rpm / MAX_RPM;
	}
	
	@Override
	public void remove() {
		super.remove();
		lazyEnergy.invalidate();
	}
}