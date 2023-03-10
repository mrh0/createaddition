package com.mrh0.createaddition.energy;

import java.util.HashMap;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.debug.AdditionDebugger;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.util.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface IWireNode {
	
	int MAX_LENGTH = Config.CONNECTOR_MAX_LENGTH.get();

	/**
	 * Get the {@link IWireNode} at the given index.
	 *
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The {@link IWireNode} at the given index, or null if the node
	 *          doesn't exist.
	 */
	@Nullable
	IWireNode getWireNode(int index);

	/**
	 * Used by {@link IWireNode#getWireNode(int)} to get a cached
	 * {@link IWireNode}.
	 */
	@Nullable
	static IWireNode getWireNodeFrom(int index, IWireNode obj, LocalNode[] localNodes, IWireNode[] nodeCache,
	                                 Level level) {
		if (!obj.hasConnection(index)) return null;
		// Cache the node if it isn't already.
		if (nodeCache[index] == null)
			nodeCache[index] = IWireNode.getWireNode(level, localNodes[index].getPos());
		// If the node is still null, remove it.
		if (nodeCache[index] == null) {
			AdditionDebugger.print(level, "Removing node " + index + " from " + obj.getPos());
			obj.removeNode(index);
		}
		return nodeCache[index];
	}

	/**
	 * Get the {@link LocalNode} for the given index.
	 *
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The {@link LocalNode} for the given index, or null if the node
	 *          doesn't exist.
	 */
	@Nullable
	LocalNode getLocalNode(int index);

	/**
	 * Create a new node at the given location.
	 *
	 * @param   index
	 *          The index of the node to connect.
	 * @param   other
	 *          The index of the node to connect to.
	 * @param   pos
	 *          The position of the node we're connecting to.
	 * @param   type
	 *          The type of wire we're using.
	 */
	void setNode(int index, int other, BlockPos pos, WireType type);

	/**
	 * Remove the given node.
	 *
	 * @param   index
	 *          The index of the node to remove.
	 */
	void removeNode(int index);

	/**
	 * Get an available node index from this {@link IWireNode}, based on the
	 * given position.
	 *
	 * @param   pos
	 *          The position we want the node from.
	 *
	 * @return  The node index, or -1 if none are available.
	 */
	default int getAvailableNode(Vec3 pos) {
		// before: return 0;
		// Might be a good idea to not return 0 if the method isn't implemented.
		return getAvailableNode();
	}

	/**
	 * Get the number of nodes this {@link IWireNode} supports.
	 *
	 * @return  The number of supported nodes.
	 */
	default int getNodeCount() {
		return 1;
	}

	/**
	 * Get an available node index from this {@link IWireNode}.
	 *
	 * @return  The node index, or -1 if none are available.
	 */
	default int getAvailableNode() {
		for (int i = 0; i < getNodeCount(); i++) {
			if (hasConnection(i)) continue;
			return i;
		}
		return -1;
	}

	/**
	 * Get the position of the node at the given index.
	 *
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The position of the node, or null if the node doesn't exist.
	 */
	@Nullable
	default BlockPos getNodePos(int index) {
		LocalNode node = getLocalNode(index);
		if (node == null) return null;
		return node.getPos();
	}

	/**
	 * Get the {@link WireType} of the node at the given index.
	 *
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The {@link WireType} of the node, or null if the node doesn't
	 */
	@Nullable
	default WireType getNodeType(int index) {
		LocalNode node = getLocalNode(index);
		if (node == null) return null;
		return node.getType();
	}

	/**
	 * Get the index of the other node connected to the given index.
	 *
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The index of the other node, or -1 if the node doesn't exist.
	 */
	default int getOtherNodeIndex(int index) {
		LocalNode node = getLocalNode(index);
		if (node == null) return -1;
		return node.getOtherIndex();
	}

	/**
	 * Check if this {@link IWireNode} has a node at the given index.
	 *
	 * @param   index
	 *          The index of the node to check.
	 *
	 * @return  True if the node exists, false otherwise.
	 */
	default boolean hasConnection(int index) {
		return getLocalNode(index) != null;
	}

	/**
	 * Check if this {@link IWireNode} has a node at the given position.
	 *
	 * @param   pos
	 *          The position to check.
	 *
	 * @return  True if a node at the given position exists, false otherwise.
	 */
	default boolean hasConnectionTo(BlockPos pos) {
		if (pos == null) return false;
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode node = getLocalNode(i);
			if (node == null) continue;
			if (node.getPos().equals(pos)) return true;
		}
		return false;
	}

	/**
	 * Check if the given node index is an input node.
	 *
	 * @param   index
	 *          The index of the node to check.
	 *
	 * @return  True if the node is an input node, false otherwise.
	 */
	default boolean isNodeInput(int index) {
		return true;
	}

	/**
	 * Check if the given node index is an output node.
	 *
	 * @param   node
	 *          The index of the node to check.
	 *
	 * @return  True if the node is an output node, false otherwise.
	 */
	default boolean isNodeOutput(int node) {
		return true;
	}

	Vec3 getNodeOffset(int node);

	/**
	 * Get the position of this {@link IWireNode}.
	 *
	 * @return  The position of this {@link IWireNode}.
	 */
	BlockPos getPos();

	void invalidateNodeCache();

	// Energy Network

	void setNetwork(int node, EnergyNetwork network);
	EnergyNetwork getNetwork(int node);

	default boolean awakeNetwork(Level world) {
		boolean b = false;
		for(int i = 0; i < getNodeCount(); i++) {
			if(!isNetworkValid(i)) {
				setNetwork(i, EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, i));
				b = true;
			}
		}
		return b;
	}

	default boolean isNetworkValid(int node) {
		if(getNetwork(node) == null)
			return false;
		else
			return getNetwork(node).isValid();
	}

	// Static Helpers

	/**
	 * Get the position of the node at the given index by reading the
	 * {@link CompoundTag}.
	 *
	 * @param   nbt
	 *          The {@link CompoundTag} to read from.
	 * @param   index
	 *          The index of the node.
	 * @param   origin
	 *          The origin {@link BlockPos} to offset from.
	 *
	 * @return  The {@link BlockPos} of the node, or null if the node doesn't
	 *          exist.
	 */
	@Nullable
	static BlockPos readNodeBlockPos(CompoundTag nbt, int index, BlockPos origin) {
		CompoundTag node = getNbtNode(nbt, index);
		if (node == null) return null;
		return origin.offset(node.getInt(LocalNode.X), node.getInt(LocalNode.Y), node.getInt(LocalNode.Z));
	}

	/**
	 * Get the {@link WireType} of the node at the given index by reading the
	 * {@link CompoundTag}.
	 *
	 * @param   nbt
	 *          The {@link CompoundTag} to read from.
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The {@link WireType} of the node, or null if the node doesn't
	 *          exist.
	 */
	static WireType readNodeWireType(CompoundTag nbt, int index) {
		CompoundTag node = getNbtNode(nbt, index);
		if (node == null) return null;
		return WireType.fromIndex(node.getInt(LocalNode.TYPE));
	}

	/**
	 * Get the index of the other node connected to the given index by reading
	 * the {@link CompoundTag}.
	 *
	 * @param   nbt
	 *          The {@link CompoundTag} to read from.
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The index of the other node, or -1 if the node doesn't exist.
	 */
	static int readNodeOtherIndex(CompoundTag nbt, int index) {
		CompoundTag node = getNbtNode(nbt, index);
		if (node == null) return -1;
		return node.getInt(LocalNode.OTHER);
	}

	/**
	 * Get the {@link CompoundTag} for the node at the given index.
	 *
	 * @param   nbt
	 *          The {@link CompoundTag} to read from.
	 * @param   index
	 *          The index of the node.
	 *
	 * @return  The {@link CompoundTag} for the node, with all the data inside,
	 *          or null if the node doesn't exist.
	 */
	@Nullable
	static CompoundTag getNbtNode(CompoundTag nbt, int index) {
		if (!nbt.contains(LocalNode.NODES)) return null;
		ListTag nodes = nbt.getList(LocalNode.NODES, Tag.TAG_COMPOUND);
		for (Tag t : nodes) {
			CompoundTag node = (CompoundTag) t;
			if (node.getInt(LocalNode.ID) == index) {
				return node;
			}
		}
		return null;
	}

	// TODO: Self reminder to clean.
	
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
		
		wn1.setNode(node1, node2, wn2.getPos(), type);
		wn2.setNode(node2, node1, wn1.getPos(), type);
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
			dropWire(world, getPos(), stack);
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
				dropWire(world, getPos(), player.getInventory().add(stack) ? ItemStack.EMPTY : stack);
		}
		for(ItemStack stack : stacks2) {
			if(!stack.isEmpty())
				dropWire(world, getPos(), player.getInventory().add(stack) ? ItemStack.EMPTY : stack);
		}
	}
	
	// Energy Network:
	
	public default boolean isNodeIndeciesConnected(int in, int other) {
		return true;
	}
}
