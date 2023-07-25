/*
package com.mrh0.createaddition.blocks.energy_meter;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.mrh0.createaddition.compat.computercraft.RedstoneRelayPeripheral;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnergyMeterTileEntity extends SmartBlockEntity implements IWireNode, IHaveGoggleInformation, IObserveTileEntity {

	//private final InternalEnergyStorage energyBufferIn;
	//private final InternalEnergyStorage energyBufferOut;

	private final Set<LocalNode> wireCache = new HashSet<>();
	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;
	private EnergyNetwork networkIn;
	private EnergyNetwork networkOut;
	private int demand = 0;
	private int throughput = 0;

	private boolean wasContraption = false;
	private boolean firstTick = true;
	
	public static Vec3 OFFSET_NORTH = new Vec3(	0f, 	-1f/16f, 	-5f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(	-5f/16f, 	-1f/16f, 	0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(	0f, 	-1f/16f, 	5f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(	5f/16f, 	-1f/16f, 	0f);
	
	public static Vec3 IN_VERTICAL_OFFSET_NORTH = new Vec3(	5f/16f, 	0f, 	-1f/16f);
	public static Vec3 IN_VERTICAL_OFFSET_WEST = new Vec3(	-1f/16f, 	0f, 	-5f/16f);
	public static Vec3 IN_VERTICAL_OFFSET_SOUTH = new Vec3(	-5f/16f, 	0f, 	1f/16f);
	public static Vec3 IN_VERTICAL_OFFSET_EAST = new Vec3(	1f/16f, 	0f, 	5f/16f);
	
	public static Vec3 OUT_VERTICAL_OFFSET_NORTH = new Vec3(	-5f/16f, 	0f, 	-1f/16f);
	public static Vec3 OUT_VERTICAL_OFFSET_WEST = new Vec3(	-1f/16f, 	0f, 	5f/16f);
	public static Vec3 OUT_VERTICAL_OFFSET_SOUTH = new Vec3(	5f/16f, 	0f, 	1f/16f);
	public static Vec3 OUT_VERTICAL_OFFSET_EAST = new Vec3(	1f/16f, 	0f, 	-5f/16f);
	
	public static final int NODE_COUNT = 8;

	protected LazyOptional<RedstoneRelayPeripheral> peripheral;

	public EnergyMeterTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);

		this.localNodes = new LocalNode[getNodeCount()];
		this.nodeCache = new IWireNode[getNodeCount()];

		//if (CreateAddition.CC_ACTIVE)
		//	this.peripheral = LazyOptional.of(() -> Peripherals.createRedstoneRelayPeripheral(this));
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> list) {

	}

	@Override
	public @Nullable IWireNode getWireNode(int index) {
		return IWireNode.getWireNodeFrom(index, this, this.localNodes, this.nodeCache, level);
	}

	@Override
	public @Nullable LocalNode getLocalNode(int index) {
		return this.localNodes[index];
	}

	@Override
	public void setNode(int index, int other, BlockPos pos, WireType type) {
		this.localNodes[index] = new LocalNode(this, index, other, type, pos);

		notifyUpdate();

		// Invalidate
		if (networkIn != null) networkIn.invalidate();
		if (networkOut != null) networkOut.invalidate();
	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;

		invalidateNodeCache();
		notifyUpdate();

		// Invalidate
		if (networkIn != null) networkIn.invalidate();
		if (networkOut != null) networkOut.invalidate();
		// Drop wire next tick.
		if (dropWire && old != null) this.wireCache.add(old);
	}

	@Override
	public int getNodeCount() {
		return NODE_COUNT;
	}
	
	@Override
	public Vec3 getNodeOffset(int node) {
		boolean vertical = getBlockState().getValue(EnergyMeterBlock.VERTICAL);
		Direction direction = getBlockState().getValue(EnergyMeterBlock.HORIZONTAL_FACING);
		// Output
		if(node > 3) {
			return switch (direction) {
				case NORTH -> vertical ? OUT_VERTICAL_OFFSET_NORTH : OFFSET_NORTH;
				case WEST -> vertical ? OUT_VERTICAL_OFFSET_WEST : OFFSET_WEST;
				case SOUTH -> vertical ? OUT_VERTICAL_OFFSET_SOUTH : OFFSET_SOUTH;
				case EAST -> vertical ? OUT_VERTICAL_OFFSET_EAST : OFFSET_EAST;
				default -> OFFSET_NORTH;
			};
		}
		// Input
		return switch (direction) {
			case NORTH -> vertical ? IN_VERTICAL_OFFSET_NORTH : OFFSET_SOUTH;
			case WEST -> vertical ? IN_VERTICAL_OFFSET_WEST : OFFSET_EAST;
			case SOUTH -> vertical ? IN_VERTICAL_OFFSET_SOUTH : OFFSET_NORTH;
			case EAST -> vertical ? IN_VERTICAL_OFFSET_EAST : OFFSET_WEST;
			default -> OFFSET_NORTH;
		};
	}

	@Override
	public int getAvailableNode(Vec3 pos) {
		Direction dir = level.getBlockState(worldPosition).getValue(EnergyMeterBlock.HORIZONTAL_FACING);
		boolean vertical = level.getBlockState(worldPosition).getValue(EnergyMeterBlock.VERTICAL);
		boolean upper = true;
		pos = pos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		if (vertical) {
			switch (dir) {
				case NORTH -> upper = pos.x() < 0.5d;
				case WEST -> upper = pos.z() > 0.5d;
				case SOUTH -> upper = pos.x() > 0.5d;
				case EAST -> upper = pos.z() < 0.5d;
				default -> {}
			}
		} else {
			switch (dir) {
				case NORTH -> upper = pos.z() < 0.5d;
				case WEST -> upper = pos.x() < 0.5d;
				case SOUTH -> upper = pos.z() > 0.5d;
				case EAST -> upper = pos.x() > 0.5d;
				default -> {}
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
	public boolean isNodeInput(int node) {
		return node < 4;
	}
	
	@Override
	public boolean isNodeOutput(int node) {
		return !isNodeInput(node);
	}

	@Override
	public BlockPos getPos() {
		return getBlockPos();
	}

	@Override
	public EnergyNetwork getNetwork(int node) {
		return isNodeInput(node) ? networkIn : networkOut;
	}

	@Override
	public void setNetwork(int node, EnergyNetwork network) {
		if(isNodeInput(node))
			networkIn = network;
		if(isNodeOutput(node))
			networkOut = network;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		// Convert old nbt data. x0, y0, z0, node0 & type0 etc.
		if (!clientPacket && nbt.contains("node0")) {
			convertOldNbt(nbt);
			setChanged();
		}

		// Read the nodes.
		invalidateLocalNodes();
		invalidateNodeCache();
		ListTag nodes = nbt.getList(LocalNode.NODES, Tag.TAG_COMPOUND);
		nodes.forEach(tag -> {
			LocalNode localNode = new LocalNode(this, (CompoundTag) tag);
			this.localNodes[localNode.getIndex()] = localNode;
		});

		// Check if this was a contraption.
		if (nbt.contains("contraption") && !clientPacket) {
			this.wasContraption = nbt.getBoolean("contraption");
			NodeRotation rotation = getBlockState().getValue(NodeRotation.ROTATION);
			if (rotation != NodeRotation.NONE)
				level.setBlock(getBlockPos(), getBlockState().setValue(NodeRotation.ROTATION, NodeRotation.NONE), 0);
			// Loop over all nodes and update their relative positions.
			for (LocalNode localNode : this.localNodes) {
				if (localNode == null) continue;
				localNode.updateRelative(rotation);
			}
		}

		// Invalidate the network if we updated the nodes.
		if (!nodes.isEmpty() && this.networkIn != null && this.networkOut != null) {
			this.networkIn.invalidate();
			this.networkOut.invalidate();
		}
	}
	
	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		// Write nodes.
		ListTag nodes = new ListTag();
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = this.localNodes[i];
			if (localNode == null) continue;
			CompoundTag tag = new CompoundTag();
			localNode.write(tag);
			nodes.add(tag);
		}
		nbt.put(LocalNode.NODES, nodes);
	}

	// Called after the tile entity has been part of a contraption.
	// Only runs on the server.
	private void validateNodes() {
		boolean changed = validateLocalNodes(this.localNodes);

		// Always set as changed if we were a contraption, as nodes might have been rotated.
		notifyUpdate();

		if (changed) {
			invalidateNodeCache();
			// Invalidate
			if (networkIn != null) networkIn.invalidate();
			if (networkOut != null) networkOut.invalidate();
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.firstTick) {
			this.firstTick = false;
			// Check if this blockentity was a part of a contraption.
			// If it was, then make sure all the nodes are valid.
			if (this.wasContraption && !level.isClientSide()) {
				this.wasContraption = false;
				validateNodes();
			}
		}

		// Check if we need to drop any wires due to contraption.
		if (!this.wireCache.isEmpty() && !isRemoved()) handleWireCache(level, this.wireCache);

		if (level.isClientSide()) return;
		networkTick();
	}

	private void networkTick() {
		if(awakeNetwork(level)) notifyUpdate();
		BlockState bs = getBlockState();
		throughput = 0;
		if(!bs.is(CABlocks.REDSTONE_RELAY.get())) return;
		if(bs.getValue(EnergyMeterBlock.POWERED)) {
			throughput = networkOut.push(networkIn.pull(demand));
			demand = networkIn.demand(networkOut.getDemand());
		}
	}

	public int getThroughput() {
		return throughput;
	}

	@Override
	public void remove() {
		if (level.isClientSide()) return;
		// Remove all nodes.
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = getLocalNode(i);
			if (localNode == null) continue;
			IWireNode otherNode = getWireNode(i);
			if (otherNode == null) continue;

			int ourNode = localNode.getOtherIndex();
			if (localNode.isInvalid()) otherNode.removeNode(ourNode);
			else otherNode.removeNode(ourNode, true); // Make the other node drop the wires.
		}

		invalidateNodeCache();
		invalidateCaps();

		// Invalidate
		if (networkIn != null) networkIn.invalidate();
		if (networkOut != null) networkOut.invalidate();
	}

	public void invalidateLocalNodes() {
		for(int i = 0; i < getNodeCount(); i++)
			this.localNodes[i] = null;
	}

	@Override
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			this.nodeCache[i] = null;
	}
	
	@Override
	public boolean isNodeIndeciesConnected(int in, int other) {
		return isNodeInput(in) == isNodeInput(other);
	}

	public int getDemand() {
		return demand;
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		if(isNetworkValid(pack.getNode()))
			EnergyNetworkPacket.send(worldPosition, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
		if (CreateAddition.CC_ACTIVE && Peripherals.isPeripheral(cap)) return this.peripheral.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		@SuppressWarnings("resource")
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray == null)
			return false;
		int node = getAvailableNode(ray.getLocation());
		
		ObservePacket.send(worldPosition, node);
		
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.relay.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.selected").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" "))
				.append(new TranslatableComponent(isNodeInput(node) ? "createaddition.tooltip.energy.input" : "createaddition.tooltip.energy.output").withStyle(ChatFormatting.AQUA)));
		
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(" ")
				.append(Util.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));
		
		return true;
	}
}
*/