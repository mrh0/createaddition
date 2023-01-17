package com.mrh0.createaddition.energy;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

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

public abstract class BaseElectricTileEntity extends SmartTileEntity {

	protected final InternalEnergyStorage localEnergy;
	protected LazyOptional<IEnergyStorage> lazyEnergy;
	
	private boolean firstTickState = true;
	protected final int CAPACITY, MAX_IN, MAX_OUT;
	
	public BaseElectricTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int CAPACITY, int MAX_IN, int MAX_OUT) {
		super(tileEntityTypeIn, pos, state);
		localEnergy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		this.CAPACITY = CAPACITY;
		this.MAX_IN = MAX_IN;
		this.MAX_OUT = MAX_OUT;
		lazyEnergy = LazyOptional.of(() -> localEnergy);
		setLazyTickRate(20);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}
	
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
		updateBlocked--;
		if(updateBlocked < 0)
			updateBlocked = 0;
		super.tick();
		if(firstTickState)
			firstTick();
		firstTickState = false;
	}
	
	public void firstTick() {
		updateCache();
	};
	
	public void updateCache() {
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
			updateCache(side);
		}
	}
	
	int updateBlocked = 0;
	
	public void updateCache(Direction side) {
		if(updateBlocked > 10) return;
		updateBlocked++;
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
		if(te == null) {
			setCache(side, LazyOptional.empty());
			return;
		}
		LazyOptional<IEnergyStorage> le = te.getCapability(ForgeCapabilities.ENERGY, side.getOpposite());
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
	

	public boolean isValidUpgradeSide(BlockState state, Direction side) {
		return false;
	}
	
	public float getBoostPerUpgrade() {
		return 0f;
	}
}
