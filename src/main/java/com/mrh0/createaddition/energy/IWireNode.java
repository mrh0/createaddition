package com.mrh0.createaddition.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

public interface IWireNode {
	public Vector3f getNodePosition(int node);
	public IEnergyStorage getNodeEnergyStorage(int node);
	public default boolean canNodeExtract(int node) {
		return getNodeEnergyStorage(node).canExtract();
	}
	
	public default boolean canNodeReceive(int node) {
		return getNodeEnergyStorage(node).canReceive();
	}
	
	public default int getNodeFromPos(Vector3f pos) {
		return 0;
	}
	
	public default int getNodeCount() {
		return 1;
	}
	
	public static boolean hasPos(CompoundNBT nbt, int node) {
		return nbt.contains("x"+node) && nbt.contains("y"+node) && nbt.contains("z"+node);
	}
	
	public static boolean hasNode(CompoundNBT nbt, int node) {
		return hasNode(nbt, node) && nbt.contains("type"+node);
	}
	
	public default int findOpenNode(int from, int to) {
		for(int i = from; i < to; i++ ) {
			if(!hasConnection(i))
				return i;
		}
		return -1;
	}
	
	public default CompoundNBT writeNode(CompoundNBT nbt, int node) {
		BlockPos pos = getNodePos(node);
		int index = getNodeIndex(node);
		WireType type = getNodeType(node);
		nbt.putInt("x"+node, pos.getX());
		nbt.putInt("y"+node, pos.getY());
		nbt.putInt("z"+node, pos.getZ());
		nbt.putInt("node"+node, index);
		nbt.putInt("type"+node, type.getIndex());
		return nbt;
	}
	
	public default void readNode(CompoundNBT nbt, int node) {
		BlockPos pos = new BlockPos(nbt.getInt("x"+node), nbt.getInt("y"+node), nbt.getInt("z"+node));
		WireType type =  WireType.fromIndex(nbt.getInt("type"+node));
		int index = nbt.getInt("node");
		setNode(node, index, pos, type);
	}
	
	public default void removeNode(CompoundNBT nbt, int node) {
		nbt.remove("x"+node);
		nbt.remove("y"+node);
		nbt.remove("z"+node);
		nbt.remove("type"+node);
	}
	
	public void setNode(int node, int other, BlockPos pos, WireType type);
	public BlockPos getNodePos(int node);
	public WireType getNodeType(int node);
	public int getNodeIndex(int node);
	public default boolean hasConnection(int node) {
		return getNodePos(node) != null;
	}
	
	public BlockPos getMyPos();
	
	public static boolean connect(World world, BlockPos pos1, int node1, BlockPos pos2, int node2, WireType type) {
		TileEntity te1 = world.getTileEntity(pos1);
		if(te1 == null)
			return false;
		TileEntity te2 = world.getTileEntity(pos1);
		if(te2 == null)
			return false;
		if(!(te1 instanceof IWireNode))
			return false;
		if(!(te2 instanceof IWireNode))
			return false;
		
		IWireNode wn1 = (IWireNode) te1;
		IWireNode wn2 = (IWireNode) te2;
		
		
		
		return true;
	}
}
