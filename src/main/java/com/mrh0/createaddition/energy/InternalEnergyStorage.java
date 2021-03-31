package com.mrh0.createaddition.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalEnergyStorage extends EnergyStorage {
	public InternalEnergyStorage(int capacity) {
        super(capacity, capacity, capacity, 0);
    }

    public InternalEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public InternalEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    public InternalEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }
    
    public CompoundNBT write(CompoundNBT nbt) {
    	nbt.putInt("energy", energy);
    	return nbt;
    }
    
    public void read(CompoundNBT nbt) {
    	setEnergy(nbt.getInt("energy"));
    }
    
    public CompoundNBT write(CompoundNBT nbt, String name) {
    	nbt.putInt("energy_"+name, energy);
    	return nbt;
    }
    
    public void read(CompoundNBT nbt, String name) {
    	setEnergy(nbt.getInt("energy_"+name));
    }
    
    @Override
    public boolean canExtract() {
    	return true;
    }
    
    @Override
    public boolean canReceive() {
    	return true;
    }
    
    public int internalConsumeEnergy(int consume) {
    	int oenergy = energy;
        energy = Math.max(0, energy - consume);
        return oenergy - energy;
    }
    
    public int internalProduceEnergy(int produce) {
    	int oenergy = energy;
        energy = Math.min(capacity, energy + produce);
        return oenergy - energy;
    }
    
    public void setEnergy(int energy) {
    	this.energy = energy;
    }
    
    @Deprecated
    public void outputToSide(World world, BlockPos pos, Direction side, int max) {
    	TileEntity te = world.getTileEntity(pos.offset(side));
		if(te == null)
			return;
		LazyOptional<IEnergyStorage> opt = te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
		IEnergyStorage ies = opt.orElse(null);
		if(ies == null)
			return;
		int ext = this.extractEnergy(max, false);
		this.receiveEnergy(ext - ies.receiveEnergy(ext, false), false);
    }
    
    @Override
    public String toString() {
    	return getEnergyStored() + "/" + getMaxEnergyStored();
    }
}
