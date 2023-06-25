package com.mrh0.createaddition.energy;

import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public abstract class BaseElectricTileEntity extends SmartBlockEntity implements EnergyTransferable {

	protected final InternalEnergyStorage energy;
	protected LazyOptional<EnergyStorage> lazyEnergy;
	
	private boolean firstTickState = true;
	protected final long CAPACITY, MAX_IN, MAX_OUT;
	
	public BaseElectricTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, long CAPACITY, long MAX_IN, long MAX_OUT) {
		super(tileEntityTypeIn, pos, state);
		energy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		this.CAPACITY = CAPACITY;
		this.MAX_IN = MAX_IN;
		this.MAX_OUT = MAX_OUT;
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	@Nullable
	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction side) {
		if(isEnergyInput(side) || isEnergyOutput(side)) {
			return lazyEnergy.getValueUnsafer();
		}
		return null;
	}
	
	public abstract boolean isEnergyInput(Direction side);

	public abstract boolean isEnergyOutput(Direction side);
	
	
	@Override
	protected void read(CompoundTag compound, boolean arg1) {
		super.read(compound, arg1);
		energy.read(compound);
	}
	
	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
	}
	
	@Deprecated
	public void outputTick(int max) {
		for(Direction side : Direction.values()) {
			if(!isEnergyOutput(side))
				continue;
			energy.outputToSide(level, worldPosition, side, max);
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if(firstTickState)
			firstTick();
		firstTickState = false;
	}
	
	public void firstTick() {
		updateCache();
	}

	public void updateCache() {
		if (level != null) {
			if (level.isClientSide)
				return;
			for (Direction side : Direction.values()) {
				BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
				if (te == null) {
					setCache(side, LazyOptional.empty());
					continue;
				}
				LazyOptional<EnergyStorage> le = LazyOptional.ofObject(EnergyStorage.SIDED.find(te.getLevel(), te.getBlockPos(), side.getOpposite()));
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
}
