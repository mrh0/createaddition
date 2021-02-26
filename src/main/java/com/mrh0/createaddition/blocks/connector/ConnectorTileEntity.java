package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ConnectorTileEntity extends BaseElectricTileEntity implements IWireNode {

	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	
	public static Vector3f OFFSET_DOWN = new Vector3f(0f, -3f/16f, 0f);
	public static Vector3f OFFSET_UP = new Vector3f(0f, 3f/16f, 0f);
	public static Vector3f OFFSET_NORTH = new Vector3f(0f, 0f, -3f/16f);
	public static Vector3f OFFSET_WEST = new Vector3f(-3f/16f, 0f, 0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(0f, 0f, 3f/16f);
	public static Vector3f OFFSET_EAST = new Vector3f(3f/16f, 0f, 0f);
	
	public IWireNode[] nodeCache;
	
	private static final int CAPACITY = Config.CONNECTOR_CAPACITY.get(), MAX_IN = Config.CONNECTOR_MAX_INPUT.get(), MAX_OUT = Config.CONNECTOR_MAX_OUTPUT.get();
	
	public ConnectorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);
		setLazyTickRate(20);
		
		connectionPos = new BlockPos[getNodeCount()];
		connectionIndecies = new int[getNodeCount()];
		connectionTypes = new WireType[getNodeCount()];
		
		nodeCache = new IWireNode[getNodeCount()];
	}
	
	public IWireNode getNode(int node) {
		//return IWireNode.getWireNode(world, getNodePos(node));
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
		switch(getBlockState().get(ConnectorBlock.FACING)) {
			case DOWN:
				return OFFSET_DOWN;
			case UP:
				return OFFSET_UP;
			case NORTH:
				return OFFSET_NORTH;
			case WEST:
				return OFFSET_WEST;
			case SOUTH:
				return OFFSET_SOUTH;
			case EAST:
				return OFFSET_EAST;
		}
		return OFFSET_DOWN;
	}

	@Override
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return energy;
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return getBlockState().get(ConnectorBlock.FACING) == side;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return getBlockState().get(ConnectorBlock.FACING) == side;
	}
	
	@Override
	public int getNodeCount() {
		return 4;
	}
	
	@Override
	public int getNodeFromPos(Vec3d vector3d) {
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
	protected void read(CompoundNBT compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		for(int i = 0; i < getNodeCount(); i++)
			if(IWireNode.hasNode(compound, i))
				readNode(compound, i);
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
	public void lazyTick() {
		super.lazyTick();
		
		/*int[] a = new int[getNodeCount()];
		
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null) {
				a[i] = 0;
				continue;
			}
			a[i] = calculateOutput(getNode(i).getNodeEnergyStorage(getNodeIndex(i)));
		}
		
		String debug = "";
		for(int i = 0; i < getNodeCount(); i++)
			debug += getNode(i) + ":";
		System.out.println(debug);
		algo(a);*/
		
		
		// Shitty code:
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode n = getNode(i);
			if(n == null)
				continue;
			IEnergyStorage es = n.getNodeEnergyStorage(getNodeIndex(i));
			int ext = energy.getEnergyStored()-es.getEnergyStored();
			ext = energy.extractEnergy(ext, false);
			es.receiveEnergy(Math.max(ext, 0), false);
		}
		
		for(Direction d : Direction.values()) {
			if(!isEnergyOutput(d))
				continue;
			TileEntity te = world.getTileEntity(pos.offset(d));
			if(te == null)
				return;
			LazyOptional<IEnergyStorage> opt = te.getCapability(CapabilityEnergy.ENERGY, d.getOpposite());
			IEnergyStorage ies = opt.orElse(null);
			if(ies == null)
				return;
			int ext = energy.extractEnergy(Integer.MAX_VALUE, false);
			int rest = ext-ies.receiveEnergy(ext, false);
			int back = energy.receiveEnergy(rest, false);
		}
	}

	@Override
	public BlockPos getMyPos() {
		return pos;
	}
	
	@Override
	public void remove() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			node.removeNode(getNodeIndex(i));
			node.invalidateNodeCache();
		}
		invalidateNodeCache();
		invalidateCaps();
		super.remove();
	}

	@Override
	public void setCache(Direction side, IEnergyStorage storage) {
	}

	@Override
	public IEnergyStorage getCachedEnergy(Direction side) {
		return null;
	}
	
	/*protected int calculateOutput(IEnergyStorage other) {
		if(other == null)
			return 0;
		if(((double)other.getEnergyStored()/(double)other.getMaxEnergyStored()) > ((double)energy.getEnergyStored()/(double)energy.getMaxEnergyStored()))
				return 0;
		return Util.min(energy.extractEnergy(Integer.MAX_VALUE, true), other.receiveEnergy(Integer.MAX_VALUE, true), other.getMaxEnergyStored() - other.getEnergyStored());
	}
	
	protected void algo(int...a) {
		int rest = energy.extractEnergy(Integer.MAX_VALUE, false);
		double[] res = new double[a.length];
		for(int i = 0; i < a.length; i++)
			res[i] = 0;
		double ab = -1;
		for(int i = 0; i < res.length; i++) {
			
			int vi = Util.minIndex(a);
			int v = a[vi];
			if(v == 0) {
				a[vi] = Integer.MAX_VALUE;
				continue;
			}
			double k = rest/(a.length-i);
			if(k > v && ab == -1) {
				res[vi] = v;
				rest -= v;
			}
			else {
				if(ab == -1)
					ab = k;
				res[vi] = ab;
				rest -= ab;
			}
			a[vi] = Integer.MAX_VALUE;
		}
		double sum = 0;
		for(int i = 0; i < res.length; i++) {
			if(res[i] == 0) {
				output(i, 0);
				continue;
			}
			double floored = Math.floor(res[i]);
			sum += res[i] - floored;
			output(i, (int)floored);
		}
		energy.receiveEnergy(((int)Math.round(sum)+rest), false);
	}
	
	public void output(int index, int cycle) {
		if(getNodeType(index) == null)
			return;
		IEnergyStorage wn = getNode(index).getNodeEnergyStorage(getNodeIndex(index));
		output(wn, cycle);
	}
	
	protected void output(IEnergyStorage other, int cycle) {
		other.receiveEnergy(cycle, false);
	}*/
	
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
	}
}
