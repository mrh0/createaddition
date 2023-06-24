package com.mrh0.createaddition.energy;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public abstract class BaseElectricTileEntity extends SmartBlockEntity {

	protected final InternalEnergyStorage localEnergy;
	protected LazyOptional<EnergyStorage> lazyEnergy;
	private boolean firstTickState = true;
	protected final long CAPACITY, MAX_IN, MAX_OUT;

	public BaseElectricTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, long CAPACITY, long MAX_IN, long MAX_OUT) {
		super(tileEntityTypeIn, pos, state);
		localEnergy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		this.CAPACITY = CAPACITY;
		this.MAX_IN = MAX_IN;
		this.MAX_OUT = MAX_OUT;
		lazyEnergy = LazyOptional.of(() -> localEnergy);
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

	//	@Override
//	public <T> LazyOptional<T> getStorage(Capability<T> cap, Direction side) {
//		if(cap == CapabilityEnergy.ENERGY && (isEnergyInput(side) || isEnergyOutput(side)))// && !level.isClientSide
//			return lazyEnergy.cast();
//		return super.getStorage(cap, side);
//	}

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
		if(Objects.requireNonNull(level).isClientSide())
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
		LazyOptional<EnergyStorage> le = LazyOptional.ofObject(EnergyStorage.SIDED.find(te.getLevel(), te.getBlockPos(), side.getOpposite()));
		if(ignoreCapSide() && !le.isPresent()) le = te.getCapability(CapabilityEnergy.ENERGY);
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
