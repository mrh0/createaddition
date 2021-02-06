package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.energy.IEnergyStorage;

public class ConnectorTileEntity extends BaseElectricTileEntity implements IWireNode {

	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	
	public ConnectorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1600, 8196, 8196);
		setLazyTickRate(20);
		
		connectionPos = new BlockPos[getNodeCount()];
		int[] connectionIndecies = new int[getNodeCount()];
		connectionTypes = new WireType[getNodeCount()];
	}

	@Override
	public Vector3f getNodePosition(int node) {
		return new Vector3f(0f, 1f/16f, 0f);
	}

	@Override
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return energy;
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return true;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return true;
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
		/*TileEntity te = world.getTileEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof IWireNode))
			return;
		IWireNode other = (IWireNode) te;
		if()
		return true;*/
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		readNode(compound, 0);
	}
	
	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		writeNode(compound, 0);
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		outputTick(8196);
	}

	@Override
	public BlockPos getMyPos() {
		return pos;
	}
}
