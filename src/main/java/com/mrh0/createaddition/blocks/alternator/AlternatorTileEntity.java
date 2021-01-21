package com.mrh0.createaddition.blocks.alternator;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.mrh0.createaddition.create.KineticTileEntityFix;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.AllBlocks;
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
import net.minecraftforge.energy.IEnergyStorage;

public class AlternatorTileEntity extends KineticTileEntityFix {
	
	protected final InternalEnergyStorage energy;
	private LazyOptional<IEnergyStorage> lazyEnergy;
	
	private static final int maxIn = 0;
	private static final int maxOut = 2048;
	private static final int capacity = 32000;

	public AlternatorTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
		energy = new InternalEnergyStorage(capacity, maxIn, maxOut);
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").mergeStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").mergeStyle(TextFormatting.AQUA)));
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.production").mergeStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.format(getEnergyProductionRate((int) (isSpeedRequirementFulfilled() ? getSpeed() : 0)) * 20) + "fe/s ") // fix
				.mergeStyle(TextFormatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").mergeStyle(TextFormatting.DARK_GRAY)));
		added = true;
		return added;
	}
	
	public float calculateStressApplied() {
		float impact = 16;
		this.lastStressApplied = impact;
		return impact;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityEnergy.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)) && !world.isRemote)
			return lazyEnergy.cast();
		return super.getCapability(cap, side);
	}
	
	public boolean isEnergyInput(Direction side) {
		return false;
	}

	public boolean isEnergyOutput(Direction side) {
		return side != Direction.SOUTH;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		energy.read(compound);
	}
	
	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		if(world.isRemote())
			return;
		if(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())
			energy.internalProduceEnergy(getEnergyProductionRate((int)getSpeed()) * 20);
		
		for(Direction side : Direction.values()) {
			if(!isEnergyOutput(side))
				continue;
			energy.outputToSide(world, pos, side, maxOut);
		}
	}
	
	public static int getEnergyProductionRate(int rpm) {
		// TODO: Configs
		return (int)(ElectricMotorTileEntity.getEnergyConsumptionRate(Math.abs(rpm)) * 0.75);
	}
	
	/*public float calculateStressApplied() {
		float impact = (float) 16;
		//this.lastStressApplied = impact; // This is a problem!
		return impact;
	}*/
	
	@Override
	protected Block getStressConfigKey() {
		return AllBlocks.MECHANICAL_MIXER.get();
	}
	
	@Override
	public void remove() {
		super.remove();
		lazyEnergy.invalidate();
	}
}
