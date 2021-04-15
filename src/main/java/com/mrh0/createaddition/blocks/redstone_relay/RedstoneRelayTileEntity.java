package com.mrh0.createaddition.blocks.redstone_relay;

import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.index.CABlocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.energy.IEnergyStorage;

public class RedstoneRelayTileEntity extends BaseElectricTileEntity implements IWireNode {

	private final InternalEnergyStorage energyBufferIn;
	private final InternalEnergyStorage energyBufferOut;
	
	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vector3f OFFSET_NORTH = new Vector3f(	0f, 	-1f/16f, 	-5f/16f);
	public static Vector3f OFFSET_WEST = new Vector3f(	-5f/16f, 	-1f/16f, 	0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(	0f, 	-1f/16f, 	5f/16f);
	public static Vector3f OFFSET_EAST = new Vector3f(	5f/16f, 	-1f/16f, 	0f);
	
	public static Vector3f IN_VERTICAL_OFFSET_NORTH = new Vector3f(	5f/16f, 	0f, 	-1f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_WEST = new Vector3f(	-1f/16f, 	0f, 	-5f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_SOUTH = new Vector3f(	-5f/16f, 	0f, 	1f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_EAST = new Vector3f(	1f/16f, 	0f, 	5f/16f);
	
	public static Vector3f OUT_VERTICAL_OFFSET_NORTH = new Vector3f(	-5f/16f, 	0f, 	-1f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_WEST = new Vector3f(	-1f/16f, 	0f, 	5f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_SOUTH = new Vector3f(	5f/16f, 	0f, 	1f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_EAST = new Vector3f(	1f/16f, 	0f, 	-5f/16f);
	
	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public RedstoneRelayTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);

		energyBufferIn = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		energyBufferOut = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		
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
		boolean vertical = getBlockState().get(RedstoneRelay.VERTICAL);
		Direction direction = getBlockState().get(RedstoneRelay.HORIZONTAL_FACING);
		if(node > 3) {
			switch(direction) {
				case NORTH:
					return vertical ? OUT_VERTICAL_OFFSET_NORTH : OFFSET_NORTH;
				case WEST:
					return vertical ? OUT_VERTICAL_OFFSET_WEST : OFFSET_WEST;
				case SOUTH:
					return vertical ? OUT_VERTICAL_OFFSET_SOUTH : OFFSET_SOUTH;
				case EAST:
					return vertical ? OUT_VERTICAL_OFFSET_EAST : OFFSET_EAST;
			}
		}
		else {
			switch(direction) {
				case NORTH:
					return vertical ? IN_VERTICAL_OFFSET_NORTH : OFFSET_SOUTH;
				case WEST:
					return vertical ? IN_VERTICAL_OFFSET_WEST : OFFSET_EAST;
				case SOUTH:
					return vertical ? IN_VERTICAL_OFFSET_SOUTH : OFFSET_NORTH;
				case EAST:
					return vertical ? IN_VERTICAL_OFFSET_EAST : OFFSET_WEST;
			}
		}
		return OFFSET_NORTH;
	}

	@Override
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return isNodeInput(node) ? energyBufferIn : energyBufferOut;
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
	public boolean isNodeInput(int node) {
		return node < 4;
	}
	
	@Override
	public boolean isNodeOutput(int node) {
		return !isNodeInput(node);
	}
	
	@Override
	public int getNodeFromPos(Vector3d vec) {
		Direction dir = world.getBlockState(pos).get(RedstoneRelay.HORIZONTAL_FACING);
		boolean vertical = world.getBlockState(pos).get(RedstoneRelay.VERTICAL);
		boolean upper = true;
		vec = vec.subtract(pos.getX(), pos.getY(), pos.getZ());
		if(vertical) {
			switch(dir) {
			case NORTH:
				upper = vec.getX() < 0.5d;
				break;
			case WEST:
				upper = vec.getZ() > 0.5d;
				break;
			case SOUTH:
				upper = vec.getX() > 0.5d;
				break;
			case EAST:
				upper = vec.getZ() < 0.5d;
				break;
			}
		}
		else {
			switch(dir) {
				case NORTH:
					upper = vec.getZ() < 0.5d;
					break;
				case WEST:
					upper = vec.getX() < 0.5d;
					break;
				case SOUTH:
					upper = vec.getZ() > 0.5d;
					break;
				case EAST:
					upper = vec.getX() > 0.5d;
					break;
			}
		}
		
		
		for(int i = upper ? 4 : 0; i < (upper ? 8 : 4); i++) {
			if(hasConnection(i))
				continue;
			return i;
		}
		return -1;
	}

	@Override
	public BlockPos getNodePos(int node) {
		return connectionPos[node];
	}

	@Override
	public WireType getNodeType(int node) {
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
		energyBufferIn.read(nbt, "buffIn");
		energyBufferOut.read(nbt, "buffOut");
	}
	
	@Override
	public void write(CompoundNBT nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				IWireNode.clearNode(nbt, i);
			else
				writeNode(nbt, i);
		}
		energyBufferIn.write(nbt, "buffIn");
		energyBufferOut.write(nbt, "buffOut");
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
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();

		BlockState bs = world.getBlockState(pos);
		if(bs == null)
			return;
		if(!bs.isIn(CABlocks.REDSTONE_RELAY.get()))
			return;
		if(bs.get(RedstoneRelay.POWERED)) {
			int ext1 = energyBufferIn.extractEnergy(energyBufferOut.receiveEnergy(Integer.MAX_VALUE, true), false);
			energyBufferOut.receiveEnergy(ext1, false);
		}
		
		// Shitty code:
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode n = getNode(i);
			if(n == null)
				continue;
			if(!isNodeOutput(i))
				continue;
			if(!n.isNodeInput(getNodeIndex(i)))
				continue;
			
			IEnergyStorage es = n.getNodeEnergyStorage(getNodeIndex(i));
			
			int ext3 = energyBufferOut.getEnergyStored()-es.getEnergyStored();
			ext3 = energyBufferOut.extractEnergy(ext3, false);
			es.receiveEnergy(Math.max(ext3, 0), false);
		}
	}
	
	@Override
	public void remove() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			if(node == null)
				break;
			node.removeNode(getNodeIndex(i));
			node.invalidateNodeCache();
		}
		invalidateNodeCache();
		invalidateCaps();
		super.remove();
	}
}
