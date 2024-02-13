package com.mrh0.createaddition.energy;

import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.Objects;

public abstract class BaseElectricBlockEntity extends SmartBlockEntity implements EnergyTransferable{

	protected final InternalEnergyStorage localEnergy;
	private boolean firstTickState = true;
	// protected final int CAPACITY, MAX_IN, MAX_OUT;

	public BaseElectricBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		localEnergy = new InternalEnergyStorage(getCapacity(), getMaxIn(), getMaxOut());
		setLazyTickRate(20);
	}

	public abstract long getCapacity();
	public abstract long getMaxIn();
	public abstract long getMaxOut();

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	@Nullable
	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction side) {
		if(isEnergyInput(side) || isEnergyOutput(side)) {
			return localEnergy;
		}
		return null;
	}

	public abstract boolean isEnergyInput(Direction side);
	public abstract boolean isEnergyOutput(Direction side);

	@Override
	protected void read(CompoundTag compound, boolean arg1) {
		super.read(compound, arg1);
		localEnergy.read(compound);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		localEnergy.write(compound);
	}

	@Deprecated
	public void outputTick(int max) {
		for(Direction side : Direction.values()) {
			if(!isEnergyOutput(side))
				continue;
			localEnergy.outputToSide(level, worldPosition, side, max);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if(firstTickState) {
			firstTickState = false;
			firstTick();
		}
	}

	public void firstTick() {
		updateCache();
	}

	public boolean ignoreCapSide() {
		return false;
	}

	public void updateCache() {
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
            updateCache(side);
        }
    }

	public void updateCache(Direction side) {
		if (!level.isLoaded(worldPosition.relative(side))) {
			setCache(side, null);
			return;
		}
		EnergyStorage e = EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite());
		if (e == null && ignoreCapSide()) {
			e = EnergyStorage.SIDED.find(level, worldPosition.relative(side), null);
		}
		// Make sure the side isn't already cached.
		if (Objects.equals(e, getCachedEnergy(side))) return;
		setCache(side, e);
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
		switch(side) {
			case DOWN:
				return escacheDown;
			case EAST:
				return escacheEast;
			case NORTH:
				return escacheNorth;
			case SOUTH:
				return escacheSouth;
			case UP:
				return escacheUp;
			case WEST:
				return escacheWest;
		}
		return null;
	}
}
