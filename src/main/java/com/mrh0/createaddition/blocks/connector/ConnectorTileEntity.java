package com.mrh0.createaddition.blocks.connector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.debug.IDebugDrawer;
import com.mrh0.createaddition.energy.*;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.CreateClient;

import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.energy.CapabilityEnergy;
import team.reborn.energy.api.EnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ConnectorTileEntity extends BaseElectricTileEntity implements IWireNode, IObserveTileEntity, IHaveGoggleInformation, IDebugDrawer {

	private final Set<LocalNode> wireCache = new HashSet<>();
	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;
	private EnergyNetwork network;
	private int demand = 0;

	private boolean wasContraption = false;
	private boolean firstTick = true;

	public static Vec3 OFFSET_DOWN = new Vec3(0f, -3f/16f, 0f);
	public static Vec3 OFFSET_UP = new Vec3(0f, 3f/16f, 0f);
	public static Vec3 OFFSET_NORTH = new Vec3(0f, 0f, -3f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(-3f/16f, 0f, 0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(0f, 0f, 3f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(3f/16f, 0f, 0f);

	public static final int NODE_COUNT = 4;

	//public static final long CAPACITY = Config.CONNECTOR_CAPACITY.get(), MAX_IN = Config.CONNECTOR_MAX_INPUT.get(), MAX_OUT = Config.CONNECTOR_MAX_OUTPUT.get();
	public ConnectorTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, Config.CONNECTOR_CAPACITY.get(), Config.CONNECTOR_MAX_INPUT.get(), Config.CONNECTOR_MAX_OUTPUT.get());

		this.localNodes = new LocalNode[getNodeCount()];
		this.nodeCache = new IWireNode[getNodeCount()];
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
		if (network != null) network.invalidate();
	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;
		this.nodeCache[index] = null;

		invalidateNodeCache();
		notifyUpdate();

		// Invalidate
		if (network != null) network.invalidate();
		// Drop wire next tick.
		if (dropWire && old != null) this.wireCache.add(old);
	}

	@Override
	public int getNodeCount() {
		return NODE_COUNT;
	}

	@Override
	public Vec3 getNodeOffset(int node) {
		return switch (getBlockState().getValue(ConnectorBlock.FACING)) {
			case DOWN -> OFFSET_DOWN;
			case UP -> OFFSET_UP;
			case NORTH -> OFFSET_NORTH;
			case WEST -> OFFSET_WEST;
			case SOUTH -> OFFSET_SOUTH;
			case EAST -> OFFSET_EAST;
		};
	}

	@Override
	public BlockPos getPos() {
		return getBlockPos();
	}

	@Override
	public void setNetwork(int node, EnergyNetwork network) {
		this.network = network;
	}

	@Override
	public EnergyNetwork getNetwork(int node) {
		return network;
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return getBlockState().getValue(ConnectorBlock.FACING) == side;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return getBlockState().getValue(ConnectorBlock.FACING) == side;
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
		if (!nodes.isEmpty() && this.network != null) this.network.invalidate();
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
			// Invalidate
			if (this.network != null) this.network.invalidate();
		}
	}

	@Override
	public void tick() {
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

		if (getMode() == ConnectorMode.None) return;
		super.tick();

		if (level != null && level.isClientSide()) return;
		if(awakeNetwork(level)) notifyUpdate();
		networkTick(network);
	}

	private void networkTick(EnergyNetwork network) {
		ConnectorMode mode = getMode();
		if(level.isClientSide())
			return;

		Direction d = getBlockState().getValue(ConnectorBlock.FACING);
		IEnergyStorage ies = getCachedEnergy(d).orElse(null);
		if(ies == null) return;

		if (mode == ConnectorMode.Push || mode == ConnectorMode.Passive) {
			int pull = network.pull(demand);
			ies.receiveEnergy(pull, false);

			int testInsert = ies.receiveEnergy(MAX_OUT, true);
			demand = network.demand(testInsert);
		}

		if (mode == ConnectorMode.Pull) {
			int extracted = ies.extractEnergy(localEnergy.getSpace(), false);
			localEnergy.internalProduceEnergy(extracted);
		}

		if (mode == ConnectorMode.Pull || mode == ConnectorMode.Passive) {
			int testExtract = localEnergy.extractEnergy(Integer.MAX_VALUE, true);
			int push = network.push(testExtract);
			localEnergy.internalConsumeEnergy(push);
		}
	}

	@Override
	public void remove() {
		if (level.isClientSide()) return;
		// Remove all nodes.
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = getLocalNode(i);
			if (localNode == null) continue;
			IWireNode otherNode = getWireNode(i);
			if(otherNode == null) continue;

			int ourNode = localNode.getOtherIndex();
			if (localNode.isInvalid()) otherNode.removeNode(ourNode);
			else otherNode.removeNode(ourNode, true); // Make the other node drop the wires.
		}

		invalidateNodeCache();
		invalidateCaps();

		// Invalidate
		if (network != null) network.invalidate();
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

	public ConnectorMode getMode() {
		return getBlockState().getValue(ConnectorBlock.MODE);
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(worldPosition, getNetwork(0).getPulled(), getNetwork(0).getPushed(), player);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ObservePacket.send(worldPosition, 0);

		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.connector.info").withStyle(ChatFormatting.WHITE)));

		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.mode").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" "))
				.append(getBlockState().getValue(ConnectorBlock.MODE).getTooltip().withStyle(ChatFormatting.AQUA)));

		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(" ")
				.append(Util.format(EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));
		return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}

	@Override
	public boolean ignoreCapSide() {
		return this.getBlockState().getValue(ConnectorBlock.MODE).isActive();
	}

	@Override
	public void drawDebug() {
		if (level == null) return;
		// Outline all connected nodes.
		for (int i = 0; i < NODE_COUNT; i++) {
			LocalNode localNode = this.localNodes[i];
			if (localNode == null) continue;
			BlockPos pos = localNode.getPos();
			BlockState state = level.getBlockState(pos);
			VoxelShape shape = state.getBlockSupportShape(level, pos);
			int color;
			if (i == 0) color = 0xFF0000;
			else if (i == 1) color = 0x00FF00;
			else if (i == 2) color = 0x0000FF;
			else color = 0xFFFFFF;
			// Make sure the node is a connector block.
			if (!(level.getBlockEntity(pos) instanceof IWireNode)) {
				shape = Shapes.block();
				color = 0xFF00FF;
			}
			// ca_ = Create Addition
			CreateClient.OUTLINER.chaseAABB("ca_nodes_" + i, shape.bounds().move(pos)).lineWidth(0.0625F).colored(color);
		}
		// Outline connected power
		BlockEntity te = level.getBlockEntity(worldPosition.relative(getBlockState().getValue(ConnectorBlock.FACING)));
		if(te == null) return;

		var cap = te.getCapability(CapabilityEnergy.ENERGY, getBlockState().getValue(ConnectorBlock.FACING).getOpposite());
		if(ignoreCapSide() && !cap.isPresent()) cap = te.getCapability(CapabilityEnergy.ENERGY);

		if (!cap.isPresent()) return;
		VoxelShape shape = level.getBlockState(te.getBlockPos()).getBlockSupportShape(level, te.getBlockPos());
		CreateClient.OUTLINER.chaseAABB("ca_output", shape.bounds().move(te.getBlockPos())).lineWidth(0.0625F).colored(0x5B5BFF);
	}
}
