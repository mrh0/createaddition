package com.mrh0.createaddition.energy;

import java.util.List;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class BaseElectricTileEntity extends SmartTileEntity {

	protected final InternalEnergyStorage energy;
	protected LazyOptional<IEnergyStorage> lazyEnergy;
	
	private boolean firstTickState = true;
	
	public BaseElectricTileEntity(TileEntityType<?> tileEntityTypeIn, int CAPACITY, int MAX_IN, int MAX_OUT) {
		super(tileEntityTypeIn);
		energy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityEnergy.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)) && !world.isRemote)
			return lazyEnergy.cast();
		return super.getCapability(cap, side);
	}
	
	public abstract boolean isEnergyInput(Direction side);

	public abstract boolean isEnergyOutput(Direction side);
	
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
	public void remove() {
		super.remove();
		lazyEnergy.invalidate();
	}
	
	public void outputTick(int max) {
		for(Direction side : Direction.values()) {
			if(!isEnergyOutput(side))
				continue;
			energy.outputToSide(world, pos, side, max);
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if(firstTickState)
			firstTick();
		firstTickState = false;
	}
	
	public void firstTick() {};
}
