package com.mrh0.createaddition.energy;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class InternalEnergyStorage extends SimpleEnergyStorage {
	public InternalEnergyStorage(long capacity) {
        super(capacity, capacity, capacity);
    }

    public InternalEnergyStorage(long capacity, long maxTransfer) {
        super(capacity, maxTransfer, maxTransfer);
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        super(capacity, maxReceive, maxExtract);
        this.amount = energy;
    }
    
    public CompoundTag write(CompoundTag nbt) {
    	nbt.putLong("energy", amount);
    	return nbt;
    }
    
    public void read(CompoundTag nbt) {
    	setEnergy(nbt.getInt("energy"));
    }
    
    public CompoundTag write(CompoundTag nbt, String name) {
    	nbt.putLong("energy_"+name, amount);
    	return nbt;
    }
    
    public void read(CompoundTag nbt, String name) {
    	setEnergy(nbt.getInt("energy_"+name));
    }
public int getSpace() {
    	return Math.max(getMaxEnergyStored() - getEnergyStored(), 0);
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public boolean supportsInsertion() {
        return maxReceive > 0;
    }

    public long internalConsumeEnergy(long consume) {
        long oenergy = amount;
        amount = Math.max(0, amount - consume);
        return oenergy - amount;
    }

    public long internalProduceEnergy(long produce) {
        long oenergy = amount;
        amount = Math.min(capacity, amount + produce);
        return oenergy - amount;
    }
    
    public void setEnergy(long energy) {
    	this.amount = energy;
    }
    
    public void setCapacity(int capacity) {
    	this.capacity = capacity;
    }

    @Deprecated
    public void outputToSide(Level world, BlockPos pos, Direction side, int max) {
		EnergyStorage ies = EnergyStorage.SIDED.find(world, pos.relative(side), side.getOpposite());
		if(ies == null)
			return;
        try(Transaction t = Transaction.openOuter()) {
            long ext = this.extract(max, t);
            this.insert(ext - ies.insert(ext, t), t);
            t.commit();
        }
    }
    
    @Override
    public String toString() {
    	return getAmount() + "/" + getCapacity() + " <-" + maxExtract + " ->" + maxReceive;
    }
}