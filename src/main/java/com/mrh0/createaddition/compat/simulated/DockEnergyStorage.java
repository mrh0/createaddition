package com.mrh0.createaddition.compat.simulated;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class DockEnergyStorage implements IEnergyStorage {

    private final BlockEntity blockEntity;
    private int energy;
    private final int capacity;
    private BlockPos connectedPos;
    private DockEnergyStorage connectedStorage;
    private boolean isPrimary;



    public DockEnergyStorage(BlockEntity  be, int capacity) {
        this.blockEntity = be;
        this.capacity = capacity;
    }

    public void connect(BlockPos pos, DockEnergyStorage other) {
        this.connectedPos = pos;
        this.connectedStorage = other;

        // invalidate so the connector cache rebuilds
        this.isPrimary = blockEntity.getBlockPos().compareTo(pos) < 0;
        if (this.blockEntity.getLevel() != null  && !blockEntity.getLevel().isClientSide()) {
            this.blockEntity.getLevel().invalidateCapabilities(this.blockEntity.getBlockPos());
        }
    }

    public void disconnect() {
        this.connectedPos = null;
        this.connectedStorage = null;
        this.isPrimary = false;

        // invalidate so the connector cache rebuilds
        if (this.blockEntity.getLevel() != null  && !blockEntity.getLevel().isClientSide()) {
            this.blockEntity.getLevel().invalidateCapabilities(this.blockEntity.getBlockPos());
        }
    }


    public boolean isConnected() {
        if (connectedPos == null) return false;
        if (blockEntity.getLevel() == null) return false;
        if (blockEntity.getLevel().isClientSide()) return  false;

        BlockEntity other = blockEntity.getLevel().getBlockEntity(connectedPos);

        if (other instanceof DockingConnectorBEAccess access) {
            return access.getEnergyStorage().connectedStorage == this;
        }
        return false;
    }


    private DockEnergyStorage getStorage() {
        return isPrimary ? this : connectedStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!isConnected()) return 0;
        DockEnergyStorage storage = getStorage();
        int canAccept = storage.capacity - storage.energy;
        int toStore = Math.min(canAccept, maxReceive);
        if (!simulate) storage.energy += toStore;
        return toStore;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!isConnected()) return 0;
        DockEnergyStorage storage = getStorage();
        int toExtract = Math.min(storage.energy, maxExtract);
        if (!simulate) storage.energy -= toExtract;
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        if (!isConnected()) return 0;
        return getStorage().energy;
    }

    @Override
    public int getMaxEnergyStored() {
        if (!isConnected()) return 0;
        return getStorage().capacity;
    }

    @Override
    public boolean canExtract() { return isConnected(); }

    @Override
    public boolean canReceive() { return isConnected(); }


    public DockEnergyStorage getConnectedStorage() {
        return connectedStorage;
    }
}
