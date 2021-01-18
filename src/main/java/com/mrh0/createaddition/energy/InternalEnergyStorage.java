package com.mrh0.createaddition.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

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
    
    @Override
    public boolean canExtract() {
    	return true;
    }
    
    @Override
    public boolean canReceive() {
    	return true;
    }
    
    public void setEnergy(int energy) {
    	this.energy = energy;
    }
}
