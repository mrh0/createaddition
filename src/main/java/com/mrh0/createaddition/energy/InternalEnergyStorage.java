package com.mrh0.createaddition.energy;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class InternalEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    public long amount = 0;
    public long capacity;
    public final long maxReceive, maxExtract;
	public InternalEnergyStorage(long capacity) {
        this(capacity, capacity, capacity);
    }

    public InternalEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        StoragePreconditions.notNegative(capacity);
        StoragePreconditions.notNegative(maxReceive);
        StoragePreconditions.notNegative(maxExtract);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public InternalEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this(capacity, maxReceive, maxExtract);
        this.amount = energy;
    }

    @Override
    protected Long createSnapshot() {
        return amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        amount = snapshot;
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
    public long getSpace() {
    	return Math.max(getCapacity() - getAmount(), 0);
    }

    @Override
    public boolean supportsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public boolean supportsInsertion() {
        return maxReceive > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long inserted = Math.min(maxReceive, Math.min(maxAmount, capacity - amount));

        if (inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            return inserted;
        }

        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);

        long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

        if (extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            return extracted;
        }

        return 0;
    }

    public long simulateExtract(long maxAmount) {
        try (Transaction t = TransferUtil.getTransaction()) {
            StoragePreconditions.notNegative(maxAmount);

            long extracted = Math.min(maxExtract, Math.min(maxAmount, amount));

            if (extracted > 0) {
                updateSnapshots(t);
                amount -= extracted;
                return extracted;
            }

            return 0;
        }
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
    
    public void setCapacity(long capacity) {
    	this.capacity = capacity;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
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