package com.mrh0.createaddition.blocks.redstone_relay;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.ConnectorType;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.energy.network.WireGraph;
import com.mrh0.createaddition.energy.network.WireNodeKind;
import com.mrh0.createaddition.network.EnergyNetworkPacketPayload;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.mrh0.createaddition.util.Util;
import com.mrh0.createaddition.network.IObserveBlockEntity;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RedstoneRelayBlockEntity extends SmartBlockEntity implements IWireNode, IHaveGoggleInformation, IObserveBlockEntity {

	//private final InternalEnergyStorage energyBufferIn;
	//private final InternalEnergyStorage energyBufferOut;

	private final Set<LocalNode> wireCache = new HashSet<>();
	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;
	private EnergyNetwork networkIn;
	private EnergyNetwork networkOut;

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

	//protected LazyOptional<RedstoneRelayPeripheral> peripheral;

	public RedstoneRelayBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);

		this.localNodes = new LocalNode[getNodeCount()];
		this.nodeCache = new IWireNode[getNodeCount()];

		// if (CreateAddition.CC_ACTIVE)
			// this.peripheral = LazyOptional.of(() -> Peripherals.createRedstoneRelayPeripheral(this));
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

		WireGraph.nodeChanged(level, this);
	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;

		invalidateNodeCache();
		notifyUpdate();

		WireGraph.nodeChanged(level, this);
		// Drop wire next tick.
		if (dropWire && old != null) this.wireCache.add(old);
	}

	@Override
	public int getNodeCount() {
		return NODE_COUNT;
	}

	@Override
	public Vec3 getNodeOffset(int node) {
		boolean vertical = getBlockState().getValue(RedstoneRelayBlock.VERTICAL);
		Direction direction = getBlockState().getValue(RedstoneRelayBlock.HORIZONTAL_FACING);
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
		Direction dir = level.getBlockState(worldPosition).getValue(RedstoneRelayBlock.HORIZONTAL_FACING);
		boolean vertical = level.getBlockState(worldPosition).getValue(RedstoneRelayBlock.VERTICAL);
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
			if(hasConnection(i)) continue;
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
		if(isNodeInput(node)) networkIn = network;
		if(isNodeOutput(node)) networkOut = network;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		// Convert old nbt data. x0, y0, z0, node0 & type0 etc.
		if (!clientPacket && tag.contains("node0")) {
			convertOldNbt(tag);
			setChanged();
		}

		// Read the nodes.
		invalidateLocalNodes();
		invalidateNodeCache();
		ListTag nodes = tag.getList(LocalNode.NODES, Tag.TAG_COMPOUND);
		nodes.forEach(itag -> {
			LocalNode localNode = new LocalNode(this, (CompoundTag) itag);
			this.localNodes[localNode.getIndex()] = localNode;
		});

		// Check if this was a contraption.
		if (tag.contains("contraption") && !clientPacket) {
			this.wasContraption = tag.getBoolean("contraption");
			NodeRotation rotation = getBlockState().getValue(NodeRotation.ROTATION);
			if (rotation != NodeRotation.NONE)
				level.setBlock(getBlockPos(), getBlockState().setValue(NodeRotation.ROTATION, NodeRotation.NONE), 0);
			// Loop over all nodes and update their relative positions.
			for (LocalNode localNode : this.localNodes) {
				if (localNode == null) continue;
				localNode.updateRelative(rotation);
			}
		}

		// Only when already in the level (e.g. /data); on chunk load the level isn't set yet and onLoad registers instead.
		if (!clientPacket) WireGraph.nodeChanged(level, this);
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(nbt, registries, clientPacket);
		// Write nodes.
		ListTag nodes = new ListTag();
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = this.localNodes[i];
			if (localNode == null) continue;
			CompoundTag newTag = new CompoundTag();
			localNode.write(newTag);
			nodes.add(newTag);
		}
		nbt.put(LocalNode.NODES, nodes);
	}

	/**
	 * Called after the tile entity has been part of a contraption.
	 * Only runs on the server.
	 */
	private void validateNodes() {
		boolean changed = validateLocalNodes(this.localNodes);

		// Always set as changed if we were a contraption, as nodes might have been rotated.
		notifyUpdate();

		if (changed) {
			invalidateNodeCache();
			WireGraph.nodeChanged(level, this);
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

	// The wire graph moves the energy, so the relay keeps working while unloaded; the networks here are for the goggles.
	private void networkTick() {
		awakeNetwork(level);
	}

	public int getThroughput() {
		return WireGraph.getBridgeThroughput(level, getBlockPos());
	}

	public int getDemand() {
		return WireGraph.getBridgeDemand(level, getBlockPos());
	}

	// Called when the block state changes in place, which is how POWERED changes.
	@Override
	@SuppressWarnings("deprecation")
	public void setBlockState(BlockState state) {
		super.setBlockState(state);
		WireGraph.nodeChanged(level, this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		WireGraph.nodeLoaded(level, this);
	}

	// Chunk unloaded or block removed.
	@Override
	public void invalidate() {
		super.invalidate();
		WireGraph.nodeUnloaded(level, this);
	}

	@Override
	public void remove() {
		if (level == null) return;
		if (level.isClientSide()) return;
		// Remove all nodes.
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = getLocalNode(i);
			if (localNode == null) continue;
			IWireNode otherNode = getWireNode(i);
			if (otherNode == null) {
				// The other end is unloaded and lets go of the wire when it next loads
				if (!localNode.isInvalid()) dropWire(level, localNode);
				continue;
			}

			int ourNode = localNode.getOtherIndex();
			LocalNode otherLocal = otherNode.getLocalNode(ourNode);
			if (otherLocal == null || !otherLocal.getPos().equals(getBlockPos())) continue;
			if (localNode.isInvalid()) otherNode.removeNode(ourNode);
			else otherNode.removeNode(ourNode, true);
		}

		invalidateNodeCache();
		// invalidateCaps();

		WireGraph.nodeRemoved(level, this);
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

	// Input and output nodes are separate networks; the wire graph relays between them.
	@Override
	public WireNodeKind getWireNodeKind() {
		return WireNodeKind.RELAY;
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.Small;
	}


	@Override
	public void onObserved(ServerPlayer player, ObservePacketPayload pack) {
		if(isNetworkValid(pack.node()))
			EnergyNetworkPacketPayload.send(worldPosition, getNetwork(pack.node()).getPulled(), getNetwork(pack.node()).getPushed(), player);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray == null) return false;
		int node = getAvailableNode(ray.getLocation());

		ObservePacketPayload.send(worldPosition, node);

		String spacing = " ";
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.relay.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.selected").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Component.translatable(isNodeInput(node) ? "createaddition.tooltip.energy.push" : "createaddition.tooltip.energy.pull").withStyle(ChatFormatting.AQUA)));

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(" ")
				.append(Util.format((int)EnergyNetworkPacketPayload.clientBuff)).append("⚡/t").withStyle(ChatFormatting.AQUA));

		return true;
	}

	@Override
	public int getMaxWireLength() {
		return CommonConfig.SMALL_CONNECTOR_MAX_LENGTH.get();
	}
}
