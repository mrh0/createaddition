package com.mrh0.createaddition.blocks.redstone_relay;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.LocalNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.util.Util;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

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
import org.jetbrains.annotations.Nullable;

public class RedstoneRelayTileEntity extends SmartTileEntity implements IWireNode, IHaveGoggleInformation, IObserveTileEntity {

	//private final InternalEnergyStorage energyBufferIn;
	//private final InternalEnergyStorage energyBufferOut;

	private final LocalNode[] localNodes;
	private final IWireNode[] _nodeCache;
	
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
	
	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public RedstoneRelayTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);

		//energyBufferIn = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		//energyBufferOut = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);

		//setLazyTickRate(20);

		this.localNodes = new LocalNode[getNodeCount()];
		this._nodeCache = new IWireNode[getNodeCount()];
	}

	@Override
	public @Nullable IWireNode getWireNode(int index) {
		return IWireNode.getWireNodeFrom(index, this, this.localNodes, this._nodeCache, level);
	}

	@Override
	public @Nullable LocalNode getLocalNode(int index) {
		return this.localNodes[index];
	}
	
	@Override
	public Vec3 getNodeOffset(int node) {
		boolean vertical = getBlockState().getValue(RedstoneRelayBlock.VERTICAL);
		Direction direction = getBlockState().getValue(RedstoneRelayBlock.HORIZONTAL_FACING);
		if(node > 3) {
			switch(direction) {
				case NORTH:
					return vertical ? OUT_VERTICAL_OFFSET_NORTH : OFFSET_NORTH;
				case WEST:
					return vertical ? OUT_VERTICAL_OFFSET_WEST : OFFSET_WEST;
				case SOUTH:
					return vertical ? OUT_VERTICAL_OFFSET_SOUTH : OFFSET_SOUTH;
				case EAST:
					return vertical ? OUT_VERTICAL_OFFSET_EAST : OFFSET_EAST;
			default:
				break;
			}
		}
		else {
			switch(direction) {
				case NORTH:
					return vertical ? IN_VERTICAL_OFFSET_NORTH : OFFSET_SOUTH;
				case WEST:
					return vertical ? IN_VERTICAL_OFFSET_WEST : OFFSET_EAST;
				case SOUTH:
					return vertical ? IN_VERTICAL_OFFSET_SOUTH : OFFSET_NORTH;
				case EAST:
					return vertical ? IN_VERTICAL_OFFSET_EAST : OFFSET_WEST;
			default:
				break;
			}
		}
		return OFFSET_NORTH;
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
		Direction dir = level.getBlockState(worldPosition).getValue(RedstoneRelayBlock.HORIZONTAL_FACING);
		boolean vertical = level.getBlockState(worldPosition).getValue(RedstoneRelayBlock.VERTICAL);
		boolean upper = true;
		pos = pos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		if(vertical) {
			switch(dir) {
			case NORTH:
				upper = pos.x() < 0.5d;
				break;
			case WEST:
				upper = pos.z() > 0.5d;
				break;
			case SOUTH:
				upper = pos.x() > 0.5d;
				break;
			case EAST:
				upper = pos.z() < 0.5d;
				break;
			default:
				break;
			}
		}
		else {
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
		
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}

	@Override
	public void removeNode(int index) {
		this.localNodes[index] = null;

		invalidateNodeCache();
		this.setChanged();

		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}

	@Override
	public BlockPos getPos() {
		return getBlockPos();
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		// TODO: Support converting from older version.
		// Read the nodes.
		ListTag nodes = nbt.getList("nodes", Tag.TAG_COMPOUND);
		nodes.forEach(tag -> {
			LocalNode localNode = new LocalNode(this, (CompoundTag) tag);
			this.localNodes[localNode.getIndex()] = localNode;
		});
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
		nbt.put("nodes", nodes);
	}
	
	@Override
	public int getNodeCount() {
		return 8;
	}

	public void invalidateLocalNodes() {
		for(int i = 0; i < getNodeCount(); i++)
			this.localNodes[i] = null;
	}

	@Override
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			this._nodeCache[i] = null;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide())
			return;
		networkTick();
	}

	/*@Override
	public void lazyTick() {
		super.lazyTick();

		BlockState bs = world.getBlockState(pos);
		if(bs == null)
			return;
		if(!bs.isIn(CABlocks.REDSTONE_RELAY.get()))
			return;
		if(bs.get(RedstoneRelay.POWERED)) {
			int ext1 = energyBufferIn.extractEnergy(energyBufferOut.receiveEnergy(Integer.MAX_VALUE, true), false);
			energyBufferOut.receiveEnergy(ext1, false);
		}
		
		// Shitty code:
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode n = getNode(i);
			if(n == null)
				continue;
			if(!isNodeOutput(i))
				continue;
			if(!n.isNodeInput(getNodeIndex(i)))
				continue;
			
			IEnergyStorage es = n.getNodeEnergyStorage(getNodeIndex(i));
			
			int ext3 = energyBufferOut.getEnergyStored()-es.getEnergyStored();
			ext3 = energyBufferOut.extractEnergy(ext3, false);
			es.receiveEnergy(Math.max(ext3, 0), false);
		}
	}*/
	
	private int demand = 0;
	private void networkTick() {
		if(awakeNetwork(level)) {
			//EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, 0);//EnergyNetwork.buildNetwork(world, this);
			causeBlockUpdate();
		}
		BlockState bs = getBlockState();
		if(!bs.is(CABlocks.REDSTONE_RELAY.get()))
			return;
		if(bs.getValue(RedstoneRelayBlock.POWERED)) {
			networkOut.push(networkIn.pull(demand));
			demand = networkIn.demand(networkOut.getDemand());
		}
	}
	
	public void onBlockRemoved(boolean set) {
		for (int i = 0; i < getNodeCount(); i++) {
			LocalNode localNode = this.localNodes[i];
			if (localNode == null) continue;
			IWireNode otherNode = getWireNode(i);
			if (otherNode == null) continue;

			int ourNode = getOtherNodeIndex(i);
			otherNode.removeNode(ourNode);
			RemoveConnectorPacket.send(otherNode.getPos(), ourNode, level);
		}
		invalidateNodeCache();
		invalidateCaps();
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
		if(set)
			setRemoved();
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

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}
	
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

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		if(isNetworkValid(pack.getNode()))
			EnergyNetworkPacket.send(worldPosition, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
	}
	
	/*@Override
	protected void setRemovedNotDueToChunkUnload() {
		onBlockRemoved(false);
		super.setRemovedNotDueToChunkUnload();
		
	}*/
}
