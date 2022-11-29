package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ConnectorTileEntity extends BaseElectricTileEntity implements IWireNode, IObserveTileEntity, IHaveGoggleInformation {

	private final BlockPos[] connectionPos;
	private final int[] connectionIndices;
	private final WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vec3 OFFSET_DOWN = new Vec3(0f, -3f/16f, 0f);
	public static Vec3 OFFSET_UP = new Vec3(0f, 3f/16f, 0f);
	public static Vec3 OFFSET_NORTH = new Vec3(0f, 0f, -3f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(-3f/16f, 0f, 0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(0f, 0f, 3f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(3f/16f, 0f, 0f);
	
	public static final int NODE_COUNT = 4;

	public static final long CAPACITY = Config.CONNECTOR_CAPACITY.get(), MAX_IN = Config.CONNECTOR_MAX_INPUT.get(), MAX_OUT = Config.CONNECTOR_MAX_OUTPUT.get();
	
	public ConnectorTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, MAX_OUT);
		
		connectionPos = new BlockPos[getNodeCount()];
		connectionIndices = new int[getNodeCount()];
		connectionTypes = new WireType[getNodeCount()];
		
		nodeCache = new IWireNode[getNodeCount()];
	}
	
	public IWireNode getNode(int node) {
		if(getNodeType(node) == null) {
			nodeCache[node] = null;
			return null;
		}
		if(nodeCache[node] == null)
			nodeCache[node] = IWireNode.getWireNode(level, getNodePos(node));
		if(nodeCache[node] == null)
			setNode(node, -1, null, null);
		
		return nodeCache[node];
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
	public boolean isEnergyInput(Direction side) {
		return getBlockState().getValue(ConnectorBlock.FACING) == side;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return getBlockState().getValue(ConnectorBlock.FACING) == side;
	}
	
	@Override
	public int getNodeCount() {
		return NODE_COUNT;
	}
	
	@Override
	public int getNodeFromPos(Vec3 vector3d) {
		for(int i = 0; i < getNodeCount(); i++) {
			if(hasConnection(i))
				continue;
			return i;
		}
		return -1;
	}

	@Override
	public BlockPos getNodePos(int node) {
		return connectionPos[node];
	}

	@Override
	public WireType getNodeType(int node) {
		return connectionTypes[node];
	}
	
	@Override
	public int getOtherNodeIndex(int node) {
		if(connectionPos[node] == null)
			return -1;
		return connectionIndices[node];
	}
	
	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {
		connectionPos[node] = pos; 
		connectionIndices[node] = other;
		connectionTypes[node] = type;
		
		// Invalidate
		if(network != null)
			network.invalidate();
	}
	
	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++)
			if(IWireNode.hasNode(nbt, i))
				readNode(nbt, i);
	}
	
	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				IWireNode.clearNode(nbt, i);
			else //?
				writeNode(nbt, i);
		}
	}
	
	@Override
	public void removeNode(int other) {
		IWireNode.super.removeNode(other);
		invalidateNodeCache();
		this.setChanged();
		
		// Invalidate
		if(network != null)
			network.invalidate();
	}

	@Override
	public BlockPos getMyPos() {
		return worldPosition;
	}

	public void onBlockRemoved() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			if(node == null)
				continue;
			int other = getOtherNodeIndex(i);
			node.removeNode(other);
			node.invalidateNodeCache();
			RemoveConnectorPacket.send(node.getMyPos(), other, level);
		}
		invalidateNodeCache();
//		invalidateCaps();
		// Invalidate
		if(network != null)
			network.invalidate();
		setRemoved();
	}
	
	@Override
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
	}
	
	@Override
	public void tick() {
		super.tick();
		if (level != null && level.isClientSide()) return;
		if(awakeNetwork(level)) {
			//EnergyNetwork.buildNetwork(world, this);
			causeBlockUpdate();
		}
		networkTick(network);
	}
	
	private EnergyNetwork network;
	
	@Override
	public EnergyNetwork getNetwork(int node) {
		return network;
	}

	@Override
	public void setNetwork(int node, EnergyNetwork network) {
		this.network = network;
	}
	
	private long demand = 0;
	private void networkTick(EnergyNetwork en) {
		if(level != null && level.isClientSide())
			return;
		// TODO: Cache
		Direction d = getBlockState().getValue(ConnectorBlock.FACING);
		EnergyStorage ies = getCachedEnergy(d);
		if(ies == null)
			return;
		
		long pull = en.pull(demand);
		try(Transaction t = Transaction.openOuter()) {
			ies.insert(pull, t);

			long testExtract, testInsert;
			try(Transaction nested = Transaction.openNested(t)) {
				testExtract = energy.extract(Long.MAX_VALUE, nested);
				testInsert = ies.insert(MAX_OUT, nested);
			}
			demand = en.demand(testInsert);


			long push = en.push(testExtract);
			energy.internalConsumeEnergy(push);
			t.commit();
		}
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
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(" ")
				.append(Util.format(EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));
		
		return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}
}
