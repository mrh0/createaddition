package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.energy.CreativeEnergyStorage;

import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class CreativeEnergyTileEntity extends CrateBlockEntity {

	protected final CreativeEnergyStorage energy;
	private LazyOptional<EnergyStorage> lazyEnergy;
	
	public CreativeEnergyTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		energy = new CreativeEnergyStorage();
		lazyEnergy = LazyOptional.of(() -> energy);
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

	@Override
	public void remove() {
		lazyEnergy.invalidate();
	}
	
	public void firstTick() {
		updateCache();
	};
	
	public void updateCache() {
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
			BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
			if(te == null) {
				setCache(side, LazyOptional.empty());
				continue;
			}
			LazyOptional<EnergyStorage> le = LazyOptional.ofObject(EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite()));
			setCache(side, le);
			
		}
	}
	
	private LazyOptional<EnergyStorage> escacheUp = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheDown = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheNorth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheEast = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheSouth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheWest = LazyOptional.empty();
	
	public void setCache(Direction side, LazyOptional<EnergyStorage> storage) {
		switch(side) {
			case DOWN:
				escacheDown = storage;
				break;
			case EAST:
				escacheEast = storage;
				break;
			case NORTH:
				escacheNorth = storage;
				break;
			case SOUTH:
				escacheSouth = storage;
				break;
			case UP:
				escacheUp = storage;
				break;
			case WEST:
				escacheWest = storage;
				break;
		}
	}
	
	public EnergyStorage getCachedEnergy(Direction side) {
		switch(side) {
			case DOWN:
				return escacheDown.orElse(null);
			case EAST:
				return escacheEast.orElse(null);
			case NORTH:
				return escacheNorth.orElse(null);
			case SOUTH:
				return escacheSouth.orElse(null);
			case UP:
				return escacheUp.orElse(null);
			case WEST:
				return escacheWest.orElse(null);
		}
		return null;
	}

	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction direction) {
		return energy;
	}
}
