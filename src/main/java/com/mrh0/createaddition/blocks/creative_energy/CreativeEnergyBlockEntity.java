package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.energy.CreativeEnergyStorage;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class CreativeEnergyBlockEntity extends CrateBlockEntity implements EnergyTransferable{

	protected final CreativeEnergyStorage energy;
	public CreativeEnergyBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		energy = new CreativeEnergyStorage();
	}
	
	private boolean firstTickState = true;
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide())
			return;
		if(firstTickState)
			firstTick();
		firstTickState = false;
		
		for(Direction d : Direction.values()) {
			EnergyStorage ies = getCachedEnergy(d);
			if(ies == null)
				continue;
			try(Transaction t = Transaction.openOuter()) {
				long r = ies.insert(Integer.MAX_VALUE, t);
				t.commit();
			}
		}
	}
	
	public void firstTick() {
		updateCache();
	}

	public void updateCache() {
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
			setCache(side, EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite()));
		}
	}
	
	private EnergyStorage escacheUp = null;
	private EnergyStorage escacheDown = null;
	private EnergyStorage escacheNorth = null;
	private EnergyStorage escacheEast = null;
	private EnergyStorage escacheSouth = null;
	private EnergyStorage escacheWest = null;
	
	public void setCache(Direction side, EnergyStorage storage) {
		switch (side) {
			case DOWN -> escacheDown = storage;
			case EAST -> escacheEast = storage;
			case NORTH -> escacheNorth = storage;
			case SOUTH -> escacheSouth = storage;
			case UP -> escacheUp = storage;
			case WEST -> escacheWest = storage;
		}
	}
	
	@SuppressWarnings("DataFlowIssue")
	public EnergyStorage getCachedEnergy(Direction side) {
		return switch (side) {
			case DOWN -> escacheDown;
			case EAST -> escacheEast;
			case NORTH -> escacheNorth;
			case SOUTH -> escacheSouth;
			case UP -> escacheUp;
			case WEST -> escacheWest;
		};
	}

	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction direction) {
		return energy;
	}
}
