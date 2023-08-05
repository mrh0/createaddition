package com.mrh0.createaddition.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class CreativeEnergyStorage implements IEnergyStorage {

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return maxExtract;
	}

	@Override
	public int getEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	public void outputToSide(Level world, BlockPos pos, Direction side) {
    	BlockEntity te = world.getBlockEntity(pos.relative(side));
		if(te == null) return;
		LazyOptional<IEnergyStorage> opt = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
		IEnergyStorage ies = opt.orElse(null);
		if(ies == null) return;
		ies.receiveEnergy(Integer.MAX_VALUE, false);
    }
}
