package com.mrh0.createaddition.blocks.accumulator;

import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.energy.IEnergyStorage;

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode {

	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vector3f OFFSET_NORTH = new Vector3f(	0f, 	0f/16f, 	0f);
	public static Vector3f OFFSET_WEST = new Vector3f(	0f, 	0f/16f, 	0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(	0f, 	0f/16f, 	0f);
	public static Vector3f OFFSET_EAST = new Vector3f(	0f, 	0f/16f, 	0f);
	
	public AccumulatorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1600, 8196, 8196);

		setLazyTickRate(20);
		
		connectionPos = new BlockPos[getNodeCount()];
		connectionIndecies = new int[getNodeCount()];
		connectionTypes = new WireType[getNodeCount()];
		
		nodeCache = new IWireNode[getNodeCount()];
	}
	
	public IWireNode getNode(int node) {
		if(getNodeType(node) == null) {
			nodeCache[node] = null;
			return null;
		}
		if(nodeCache[node] == null)
			nodeCache[node] = IWireNode.getWireNode(world, getNodePos(node));
		if(nodeCache[node] == null)
			setNode(node, -1, null, null);
		
		return nodeCache[node];
	}
	
	@Override
	public Vector3f getNodeOffset(int node) {
		if(node > 3) {
			switch(getBlockState().get(AccumulatorBlock.FACING)) {
				case NORTH:
					return OFFSET_NORTH;
				case WEST:
					return OFFSET_WEST;
				case SOUTH:
					return OFFSET_SOUTH;
				case EAST:
					return OFFSET_EAST;
			}
		}
		else {
			switch(getBlockState().get(AccumulatorBlock.FACING)) {
			case NORTH:
				return OFFSET_SOUTH;
			case WEST:
				return OFFSET_EAST;
			case SOUTH:
				return OFFSET_NORTH;
			case EAST:
				return OFFSET_WEST;
		}
		}
		return OFFSET_NORTH;
	}

	@Override
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return energy;
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != Direction.UP;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	@Override
	public int getNodeFromPos(Vector3d vector3d) {
		for(int i = 0; i < getNodeCount(); i++) {
			if(hasConnection(i))
				continue;
			return i;
		}
		return -1;
	}

	@Override
	public BlockPos getNodePos(int node) {
		if(connectionPos[node] == null)
			return null;
		return connectionPos[node];
	}

	@Override
	public WireType getNodeType(int node) {
		if(connectionPos[node] == null)
			return null;
		return connectionTypes[node];
	}
	
	@Override
	public int getNodeIndex(int node) {
		if(connectionPos[node] == null)
			return -1;
		return connectionIndecies[node];
	}
	
	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {
		connectionPos[node] = pos; 
		connectionIndecies[node] = other;
		connectionTypes[node] = type;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT nbt, boolean clientPacket) {
		super.fromTag(state, nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++)
			if(IWireNode.hasNode(nbt, i))
				readNode(nbt, i);
	}
	
	@Override
	public void write(CompoundNBT nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				IWireNode.clearNode(nbt, i);
			else //?
				writeNode(nbt, i);
		}
	}
	
	@Override
	public void removeNode(int other) {
		IWireNode.super.removeNode(other);
		invalidateNodeCache();
		this.markDirty();
	}
	
	@Override
	public int getNodeCount() {
		return 8;
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
