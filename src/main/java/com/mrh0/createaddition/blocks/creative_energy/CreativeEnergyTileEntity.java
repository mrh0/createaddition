package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.energy.CreativeEnergyStorage;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.content.logistics.block.inventories.CrateTileEntity;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class CreativeEnergyTileEntity extends CrateTileEntity implements EnergyTransferable {

	protected final CreativeEnergyStorage energy;

	public CreativeEnergyTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		energy = new CreativeEnergyStorage();
	}
	
	private boolean firstTickState = true;
	
	@Override
	public void tick() {
		super.tick();
		if (level != null) {
			if (level.isClientSide())
				return;
			if (firstTickState)
				firstTick();
			firstTickState = false;

			for (Direction d : Direction.values()) {
				EnergyStorage ies = getCachedEnergy(d);
				if (ies == null)
					continue;
				try (Transaction t = Transaction.openOuter()) {
					t.commit();
				}
			}
		}
	}
	
	public void firstTick() {
		updateCache();
	}

	public void updateCache() {
		if (level != null) {
			if (level.isClientSide())
				return;
			for (Direction side : Direction.values()) {
				BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
				if (te == null) {
					setCache(side, LazyOptional.empty());
					continue;
				}
				LazyOptional<EnergyStorage> le = LazyOptional.ofObject(EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite()));
				setCache(side, le);
			}
		}
	}
	
	private LazyOptional<EnergyStorage> escacheUp = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheDown = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheNorth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheEast = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheSouth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheWest = LazyOptional.empty();
	
	public void setCache(Direction side, LazyOptional<EnergyStorage> storage) {
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
			case DOWN -> escacheDown.orElse(null);
			case EAST -> escacheEast.orElse(null);
			case NORTH -> escacheNorth.orElse(null);
			case SOUTH -> escacheSouth.orElse(null);
			case UP -> escacheUp.orElse(null);
			case WEST -> escacheWest.orElse(null);
		};
	}

	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction direction) {
		return energy;
	}
}
