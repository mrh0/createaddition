package com.mrh0.createaddition.energy;

import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.EnumMap;
import java.util.List;

public abstract class BaseElectricBlockEntity extends SmartBlockEntity implements EnergyTransferable {

	protected final InternalEnergyStorage localEnergy;

	private final EnumMap<Direction, BlockApiCache<EnergyStorage, Direction>> escacheMap = new EnumMap<>(Direction.class);

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

	public boolean ignoreCapSide() {
		return false;
	}

    @Nullable
	public EnergyStorage getCachedEnergy(Direction side) {
        if(!(getLevel() instanceof ServerLevel serverLevel)) {
            return null;
        }
        BlockApiCache<EnergyStorage, Direction> cache = escacheMap.computeIfAbsent(side, side1 -> BlockApiCache.create(EnergyStorage.SIDED, serverLevel, getBlockPos().relative(side)));

        EnergyStorage es = cache.find(side.getOpposite());
        if(es == null && ignoreCapSide()) {
            es = cache.find(null);
        }
		return es;
	}
}
