package com.mrh0.createaddition.energy;

import com.simibubi.create.lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public class CreativeEnergyStorage implements EnergyStorage {


	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		return maxAmount;
	}

	@Override
	public long getAmount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long getCapacity() {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	public void outputToSide(Level world, BlockPos pos, Direction side) {
		LazyOptional<EnergyStorage> opt = LazyOptional.ofObject(EnergyStorage.SIDED.find(world, pos.relative(side), side.getOpposite()));
		EnergyStorage ies = opt.orElse(null);
		if(ies == null)
			return;
		try(Transaction t = Transaction.openOuter()) {
			ies.insert(Integer.MAX_VALUE, t);
			t.commit();
		}
    }
}
