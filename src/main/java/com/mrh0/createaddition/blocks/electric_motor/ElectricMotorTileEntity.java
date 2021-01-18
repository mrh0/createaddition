package com.mrh0.createaddition.blocks.electric_motor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
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

	public static final int DEFAULT_SPEED = 128;
	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private LazyOptional<net.minecraftforge.energy.IEnergyStorage> lazyEnergy;
	
	private static final int maxIn = 10;
	private static final int maxOut = 0;
	private static final int capacity = 512;

	public ElectricMotorTileEntity(TileEntityType<? extends ElectricMotorTileEntity> type) {
		super(type);
		energy = new InternalEnergyStorage(capacity, maxIn, maxOut);
		lazyEnergy = LazyOptional.of(() -> energy);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		Integer max = AllConfigs.SERVER.kinetics.maxMotorSpeed.get();

		CenteredSideValueBoxTransform slot =
			new CenteredSideValueBoxTransform((motor, side) -> motor.get(ElectricMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
		generatedSpeed.between(-max, max);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.scrollableValue = DEFAULT_SPEED;
		generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		generatedSpeed.withStepFunction(CreativeMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy").mergeStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "FE")));
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
		return convertToDirection(generatedSpeed.getValue(), getBlockState().get(ElectricMotorBlock.FACING));
	}
	
	@Override
	protected Block getStressConfigKey() {
		//AllBlocks.HAND_CRANK.get();
		//AllConfigs.SERVER.kinetics.stressValues.getCapacities().keySet().forEach((x) -> System.out.println(x));
		//System.out.println(AllConfigs.SERVER.kinetics.stressValues.getCapacityOf(CABlocks.ELECTRIC_MOTOR.get()));
		return AllBlocks.HAND_CRANK.get();//CABlocks.ELECTRIC_MOTOR.get();
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
		return true;
	}

	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	/*@Override
	public CompoundNBT getTileData() {
		CompoundNBT nbt = super.getTileData();
		this.write(nbt);
		return nbt;
	}*/
	
	@Override
	public void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		energy.read(compound);
		super.fromTag(state, compound, clientPacket);
	}
	
	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		energy.write(compound);
		super.write(compound, clientPacket);
	}
}