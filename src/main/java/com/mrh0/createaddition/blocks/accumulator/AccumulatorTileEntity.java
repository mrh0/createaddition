package com.mrh0.createaddition.blocks.accumulator;

import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.energy.IEnergyStorage;

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode {

	public AccumulatorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1600, 8196, 8196);
	}

	@Override
	public Vector3f getNodeOffset(int node) {
		return new Vector3f(0f, 1f/16f, 0f);
	}

	@Override
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return energy;
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return false;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	@Override
	public BlockPos getNodePos(int nodes) {
		return null;
	}
	
	@Override
	public WireType getNodeType(int node) {
		return null;
	}
	
	@Override
	public int getNodeIndex(int node) {
		return 0;
	}

	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {

	}

	@Override
	public BlockPos getMyPos() {
		return pos;
	}

	@Override
	public void setCache(Direction side, IEnergyStorage storage) {
	}

	@Override
	public IEnergyStorage getCachedEnergy(Direction side) {
		return null;
	}

	@Override
	public void invalidateNodeCache() {
	}
}
