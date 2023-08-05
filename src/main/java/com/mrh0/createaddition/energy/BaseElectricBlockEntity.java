package com.mrh0.createaddition.energy;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public abstract class BaseElectricBlockEntity extends SmartBlockEntity {

	protected final InternalEnergyStorage localEnergy;
	protected LazyOptional<IEnergyStorage> lazyEnergy;

	private boolean firstTickState = true;
	// protected final int CAPACITY, MAX_IN, MAX_OUT;

	public BaseElectricBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		localEnergy = new InternalEnergyStorage(getCapacity(), getMaxIn(), getMaxOut());
		lazyEnergy = LazyOptional.of(() -> localEnergy);
		setLazyTickRate(20);
	}

	public abstract int getCapacity();
	public abstract int getMaxIn();
	public abstract int getMaxOut();

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == ForgeCapabilities.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)))// && !level.isClientSide
			return lazyEnergy.cast();
		return super.getCapability(cap, side);
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

	@Override
	public void remove() {
		lazyEnergy.invalidate();
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
			setCache(side, LazyOptional.empty());
			return;
		}
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
		if(te == null) {
			setCache(side, LazyOptional.empty());
			return;
		}
		LazyOptional<IEnergyStorage> le = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
		if(ignoreCapSide() && !le.isPresent()) le = te.getCapability(ForgeCapabilities.ENERGY);
		// Make sure the side isn't already cached.
		if (le.equals(getCachedEnergy(side))) return;
		setCache(side, le);
		le.addListener((es) -> updateCache(side));
	}

	private LazyOptional<IEnergyStorage> escacheUp = LazyOptional.empty();
	private LazyOptional<IEnergyStorage> escacheDown = LazyOptional.empty();
	private LazyOptional<IEnergyStorage> escacheNorth = LazyOptional.empty();
	private LazyOptional<IEnergyStorage> escacheEast = LazyOptional.empty();
	private LazyOptional<IEnergyStorage> escacheSouth = LazyOptional.empty();
	private LazyOptional<IEnergyStorage> escacheWest = LazyOptional.empty();

	public void setCache(Direction side, LazyOptional<IEnergyStorage> storage) {
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

	public LazyOptional<IEnergyStorage> getCachedEnergy(Direction side) {
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
		return LazyOptional.empty();
	}
}
