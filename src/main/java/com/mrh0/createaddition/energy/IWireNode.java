package com.mrh0.createaddition.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
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
	
	public default int getNodeMaxConnection(int node) {
		return 4;
	}
	
	public static boolean hasPos(CompoundNBT nbt, int node) {
		return nbt.contains("x"+node) && nbt.contains("y"+node) && nbt.contains("z"+node);
	}
	
	public default CompoundNBT writeNode(CompoundNBT nbt, int node) {
		BlockPos pos = getNode(node);
		nbt.putInt("x"+node, pos.getX());
		nbt.putInt("y"+node, pos.getY());
		nbt.putInt("z"+node, pos.getZ());
		return nbt;
	}
	
	public default void readNode(CompoundNBT nbt, int node) {
		BlockPos pos = new BlockPos(nbt.getInt("x"+node), nbt.getInt("y"+node), nbt.getInt("z"+node));
		setNode(node, pos);
	}
	
	public void setNode(int node, BlockPos pos);
	public BlockPos getNode(int nodes);
}
