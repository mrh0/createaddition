package com.mrh0.createaddition.energy;

import java.util.HashMap;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.util.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface IWireNode {
	
	public static final int MAX_LENGTH = Config.CONNECTOR_MAX_LENGTH.get();
	
	public Vec3 getNodeOffset(int node);
	
	public default int getNodeFromPos(Vec3 vector3d) {
		return 0;
	}
	
	public default int getNodeCount() {
		return 1;
	}
	
	public static boolean hasPos(CompoundTag nbt, int node) {
		return nbt.contains("x"+node) && nbt.contains("y"+node) && nbt.contains("z"+node);
	}
	
	public static boolean hasNode(CompoundTag nbt, int node) {
		return hasPos(nbt, node) && nbt.contains("type"+node);
	}
	
	public default int findOpenNode(int from, int to) {
		for(int i = from; i < to; i++ ) {
			if(!hasConnection(i))
				return i;
		}
		return -1;
	}
	
	public default CompoundTag writeNode(CompoundTag nbt, int node) {
		BlockPos pos = getNodePos(node);
		if(pos == null)
			return  nbt;
		int index = getOtherNodeIndex(node);
		//System.out.println("WRITE: " + node + "->" + index);
		WireType type = getNodeType(node);
		if(type == null)
			return nbt;
		nbt.putInt("x"+node, pos.getX());
		nbt.putInt("y"+node, pos.getY());
		nbt.putInt("z"+node, pos.getZ());
		nbt.putInt("node"+node, index);
		nbt.putInt("type"+node, type.getIndex());
		return nbt;
	}
	
	public default void readNode(CompoundTag nbt, int node) {
		if(!hasNode(nbt, node))
			return;
		BlockPos pos = new BlockPos(nbt.getInt("x"+node), nbt.getInt("y"+node), nbt.getInt("z"+node));
		WireType type =  WireType.fromIndex(nbt.getInt("type"+node));
		int index = nbt.getInt("node"+node);
		//System.out.println("READ: " + node + "->" + index);
		setNode(node, index, pos, type);
	}
	
	public static void clearNode(CompoundTag nbt, int node) {
		nbt.remove("x"+node);
		nbt.remove("y"+node);
		nbt.remove("z"+node);
		nbt.remove("type"+node);
	}
	
	public void setNode(int node, int other, BlockPos pos, WireType type);
	
	public default void removeNode(int node) {
		setNode(node, -1, null, null);
	}
	
	public BlockPos getNodePos(int node);
	public WireType getNodeType(int node);
	public int getOtherNodeIndex(int node);
	public void invalidateNodeCache();
	
	public default boolean hasConnection(int node) {
		return getNodePos(node) != null;
	}
	
	public default boolean hasConnectionTo(BlockPos pos1) {
		if(pos1 == null)
			return false;
		for(int i = 0; i < getNodeCount(); i++) {
			BlockPos pos2 = getNodePos(i);
			if(pos2 == null)
				continue;
			if(pos1.equals(pos2))
				return true;
		}
		return false;
	}
	
	public static int findConnectionTo(IWireNode wn, BlockPos pos) {
		if(pos == null)
			return -1;
		for(int i = 0; i < wn.getNodeCount(); i++) {
			BlockPos pos2 = wn.getNodePos(i);
			if(pos2 == null)
				continue;
			if(pos.equals(pos2))
				return i;
		}
		return -1;
	}
	
	public BlockPos getMyPos();
	public IWireNode getNode(int node);
	
	public static WireConnectResult connect(Level world, BlockPos pos1, int node1, BlockPos pos2, int node2, WireType type) {
		BlockEntity te1 = world.getBlockEntity(pos1);
		if(te1 == null)
			return WireConnectResult.INVALID;
		BlockEntity te2 = world.getBlockEntity(pos2);
		if(te2 == null)
			return WireConnectResult.INVALID;
		if(te1 == te2)
			return WireConnectResult.INVALID;
		if(!(te1 instanceof IWireNode))
			return WireConnectResult.INVALID;
		if(!(te2 instanceof IWireNode))
			return WireConnectResult.INVALID;
		if(node1 < 0 || node2 < 0)
			return WireConnectResult.COUNT;
		//if(pos1.equals(pos2))
		//	return WireConnectResult.INVALID;
		if(pos1.distSqr(pos2) > MAX_LENGTH * MAX_LENGTH)
			return WireConnectResult.LONG;
		
		IWireNode wn1 = (IWireNode) te1;
		IWireNode wn2 = (IWireNode) te2;
		
		//System.out.println("1 : In:" + wn1.isNodeInput(node1) + " Out:" + wn1.isNodeOutput(node1));
		//System.out.println("2 : In:" + wn2.isNodeInput(node2) + " Out:" + wn2.isNodeOutput(node2));
		
		if(wn1.hasConnectionTo(pos2))
			return WireConnectResult.EXISTS;
		
		wn1.setNode(node1, node2, wn2.getMyPos(), type);
		wn2.setNode(node2, node1, wn1.getMyPos(), type);
		//System.out.println("Connected: " + node1 + " to " + node2);
		return WireConnectResult.getLink(wn2.isNodeInput(node2), wn2.isNodeOutput(node2));
	}
	
	public static WireType getTypeOfConnection(Level world, BlockPos pos1, BlockPos pos2) {
		BlockEntity te1 = world.getBlockEntity(pos1);
		if(te1 == null)
			return null;
		if(!(te1 instanceof IWireNode))
			return null;
		
		IWireNode wn1 = (IWireNode) te1;
		
		if(!wn1.hasConnectionTo(pos2))
			return null;
		
		int node1 = findConnectionTo(wn1, pos2);
		return wn1.getNodeType(node1);
	}
	
	public static WireConnectResult disconnect(Level world, BlockPos pos1, BlockPos pos2) {
		BlockEntity te1 = world.getBlockEntity(pos1);
		if(te1 == null)
			return WireConnectResult.INVALID;
		BlockEntity te2 = world.getBlockEntity(pos2);
		if(te2 == null)
			return WireConnectResult.INVALID;
		if(te1 == te2)
			return WireConnectResult.INVALID;
		if(!(te1 instanceof IWireNode))
			return WireConnectResult.INVALID;
		if(!(te2 instanceof IWireNode))
			return WireConnectResult.INVALID;
		
		IWireNode wn1 = (IWireNode) te1;
		IWireNode wn2 = (IWireNode) te2;
		
		if(!wn1.hasConnectionTo(pos2))
			return WireConnectResult.NO_CONNECTION;
		
		int node1 = findConnectionTo(wn1, pos2);
		int node2 = findConnectionTo(wn2, pos1);
		
		if(node1 < 0)
			return WireConnectResult.NO_CONNECTION;
		if(node2 < 0)
			return WireConnectResult.NO_CONNECTION;
		
		wn1.removeNode(node1);
		wn2.removeNode(node2);
		return WireConnectResult.REMOVED;
	}
	
	public static IWireNode getWireNode(Level world, BlockPos pos) {
		if(pos == null)
			return null;
		BlockEntity te = world.getBlockEntity(pos);
		if(te == null)
			return null;
		if(!(te instanceof IWireNode))
			return null;
		return (IWireNode) te;
	}
	
	public default boolean isNodeInput(int node) {
		return true;
	}
	
	public default boolean isNodeOutput(int node) {
		return true;
	}
	
	public static void dropWire(Level world, BlockPos pos, ItemStack stack) {
		Containers.dropContents(world, pos, NonNullList.of(ItemStack.EMPTY, stack));
	}
	
	public default void dropWires(Level world) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(WireType.values().length, ItemStack.EMPTY);
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			int n = getNodeType(i).getIndex();
			if(stacks.get(n).isEmpty())
				stacks.set(n, getNodeType(i).getDrop());
			else
				stacks.get(n).grow(getNodeType(i).getDrop().getCount());
		}
		for(ItemStack stack : stacks) {
			dropWire(world, getMyPos(), stack);
		}
	}
	
	public default void dropWires(Level world, Player player) {

		NonNullList<ItemStack> stacks1 = NonNullList.withSize(WireType.values().length, ItemStack.EMPTY);
		NonNullList<ItemStack> stacks2 = NonNullList.withSize(WireType.values().length, ItemStack.EMPTY);
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			int n = getNodeType(i).getIndex();
			ItemStack spools = Util.findStack(CAItems.SPOOL.get().asItem(), player.getInventory());
			if(spools.getCount() > 0) {
				if(stacks1.get(n).isEmpty())
					stacks1.set(n, getNodeType(i).getSourceDrop());
				else
					stacks1.get(n).grow(getNodeType(i).getSourceDrop().getCount());
				spools.shrink(1);
			}
			else {
				if(stacks2.get(n).isEmpty())
					stacks2.set(n, getNodeType(i).getDrop());
				else
					stacks2.get(n).grow(getNodeType(i).getDrop().getCount());
			}
		}
		for(ItemStack stack : stacks1) {
			if(!stack.isEmpty())
				dropWire(world, getMyPos(), player.getInventory().add(stack) ? ItemStack.EMPTY : stack);
		}
		for(ItemStack stack : stacks2) {
			if(!stack.isEmpty())
				dropWire(world, getMyPos(), player.getInventory().add(stack) ? ItemStack.EMPTY : stack);
		}
	}
	
	// Energy Network:
	
	public default boolean isNodeIndeciesConnected(int in, int other) {
		return true;
	}
	
	public default boolean awakeNetwork(Level world) {
		boolean b = false;
		for(int i = 0; i < getNodeCount(); i++) {
			if(!isNetworkValid(i)) {
				setNetwork(i, EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, i));
				b = true;
			}
		}
		return b;
	}
	
	public EnergyNetwork getNetwork(int node);
	public void setNetwork(int node, EnergyNetwork network);
	
	public default boolean isNetworkValid(int node) {
		if(getNetwork(node) == null) 
			return false;
		else 
			return getNetwork(node).isValid();
	}
	
	/*public default boolean isNetworkValid() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNetwork(i) == null)
				return false;
			if(!getNetwork(i).isValid())
				return false;
		}
		return true;
	}*/
	
	public default void preformRemoveOfNode(int node) {
		removeNode(node);
		invalidateNodeCache();
	}
}
