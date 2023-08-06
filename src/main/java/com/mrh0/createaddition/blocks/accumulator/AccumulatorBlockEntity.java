package com.mrh0.createaddition.blocks.accumulator;
/*
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.ConnectorType;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.*;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.util.Util;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.IComparatorOverride;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class AccumulatorBlockEntity extends BaseElectricBlockEntity implements IWireNode, IHaveGoggleInformation, IComparatorOverride, IObserveTileEntity {

	private final Set<LocalNode> wireCache = new HashSet<>();
	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;

	private boolean wasContraption = false;
	private boolean firstTick = true;

	public static Vec3 OFFSET_NORTH = new Vec3(	0f, 	9f/16f, 	-5f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(	-5f/16f, 	9f/16f, 	0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(	0f, 	9f/16f, 	5f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(	5f/16f, 	9f/16f, 	0f);

	public static final int NODE_COUNT = 8;
	//public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();

	public AccumulatorBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);

		setLazyTickRate(20);

		this.localNodes = new LocalNode[getNodeCount()];
		this.nodeCache = new IWireNode[getNodeCount()];
	}

	@Override
	public int getCapacity() {
		return Config.ACCUMULATOR_CAPACITY.get();
	}

	@Override
	public int getMaxIn() {
		return Config.ACCUMULATOR_MAX_INPUT.get();
	}

	@Override
	public int getMaxOut() {
		return Config.ACCUMULATOR_MAX_OUTPUT.get();
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
	public Vec3 getNodeOffset(int node) {
		if(node > 3) {
			switch(getBlockState().getValue(AccumulatorBlock.FACING)) {
				case NORTH:
					return OFFSET_NORTH;
				case WEST:
					return OFFSET_WEST;
				case SOUTH:
					return OFFSET_SOUTH;
				case EAST:
					return OFFSET_EAST;
			default:
				break;
			}
		}
		else {
			switch(getBlockState().getValue(AccumulatorBlock.FACING)) {
				case NORTH:
					return OFFSET_SOUTH;
				case WEST:
					return OFFSET_EAST;
				case SOUTH:
					return OFFSET_NORTH;
				case EAST:
					return OFFSET_WEST;
			default:
				break;
			}
		}
		return OFFSET_NORTH;
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
	public int getAvailableNode(Vec3 pos) {
		Direction dir = level.getBlockState(worldPosition).getValue(AccumulatorBlock.FACING);
		boolean upper = true;
		pos = pos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		switch(dir) {
			case NORTH:
				upper = pos.z() < 0.5d;
				break;
			case WEST:
				upper = pos.x() < 0.5d;
				break;
			case SOUTH:
				upper = pos.z() > 0.5d;
				break;
			case EAST:
				upper = pos.x() > 0.5d;
				break;
		default:
			break;
		}

		for(int i = upper ? 4 : 0; i < (upper ? 8 : 4); i++) {
			if(hasConnection(i))
				continue;
			return i;
		}
		return -1;
	}

	@Override
	public void setNode(int index, int other, BlockPos pos, WireType type) {
		this.localNodes[index] = new LocalNode(this, index, other, type, pos);

		notifyUpdate();

		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;
		this.nodeCache[index] = null;

		this.invalidateNodeCache();
		notifyUpdate();

		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
		// Drop wire next tick.
		if (dropWire && old != null) this.wireCache.add(old);
	}

	@Override
	public BlockPos getPos() {
		return getBlockPos();
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

	@Override
	public int getNodeCount() {
		return NODE_COUNT;
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

	private int lastComparator = 0;
	int lastEnergy = 0;

	@Override
	public void lazyTick() {
		super.lazyTick();

		int comp = getComparetorOverride();
		if(comp != lastComparator)
			level.updateNeighborsAt(worldPosition, CABlocks.ACCUMULATOR.get());
		lastComparator = comp;

		//if(energy.getEnergyStored() != lastEnergy)
		//	causeBlockUpdate();
		//lastEnergy = energy.getEnergyStored();
	}

	private boolean firstTickState = true;

	private void validateNodes() {
		boolean changed = validateLocalNodes(this.localNodes);

		// Always set as changed if we were a contraption, as nodes might have been rotated.
		notifyUpdate();

		if (changed) {
			invalidateNodeCache();
			// Invalidate
			if (this.networkIn != null) this.networkIn.invalidate();
			if (this.networkOut != null) this.networkOut.invalidate();
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

		if(firstTickState)
			firstTick();
		firstTickState = false;
		if(level.isClientSide())
			return;
		networkTick();
	}

	private int demandOut = 0;
	private int demandIn = 0;
	private void networkTick() {
		if(awakeNetwork(level)) {
			//EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, 0);//EnergyNetwork.buildNetwork(world, this);
			//causeBlockUpdate();
			notifyUpdate();
		}
		if(networkOut == null) {
			//System.out.println("WARN!");
			return;
		}


		localEnergy.extractEnergy(networkOut.push(localEnergy.extractEnergy(demandOut, true)), false);

		demandOut = networkOut.getDemand();
		localEnergy.receiveEnergy(networkIn.pull(Math.min(demandIn, localEnergy.receiveEnergy(getMaxIn(), true))), false);
		demandIn = networkIn.demand(localEnergy.receiveEnergy(getMaxIn(), true));

	}

	@Override
	public void remove() {
		if (level.isClientSide()) return;
		for(int i = 0; i < getNodeCount(); i++) {
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
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}

	private EnergyNetwork networkIn;
	private EnergyNetwork networkOut;

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
	public boolean isNodeIndeciesConnected(int in, int other) {
		return isNodeInput(in) == isNodeInput(other);
	}

	public void setEnergy(int energy) {
		this.localEnergy.setEnergy(energy);
	}

	@Override
	public int getComparetorOverride() {
		return (int)((double)localEnergy.getEnergyStored() / (double)localEnergy.getMaxEnergyStored() * 15d);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		@SuppressWarnings("resource")
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray == null)
			return false;
		int node = getAvailableNode(ray.getLocation());

		ObservePacket.send(worldPosition, node);

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.getTextComponent(localEnergy)));

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.selected").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Component.translatable(isNodeInput(node) ? "createaddition.tooltip.energy.push" : "createaddition.tooltip.energy.pull").withStyle(ChatFormatting.AQUA)));

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(" ")
				.append(Util.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));

		return true;
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(worldPosition, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
		//causeBlockUpdate();
		notifyUpdate();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.Small;
	}

	@Override
	public int getMaxWireLength() {
		return Config.SMALL_CONNECTOR_MAX_LENGTH.get();
	}
}
*/