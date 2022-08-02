package com.mrh0.createaddition.blocks.accumulator;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.util.Util;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode, IHaveGoggleInformation, IComparatorOverride, IObserveTileEntity {
	
	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vec3 OFFSET_NORTH = new Vec3(	0f, 	9f/16f, 	-5f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(	-5f/16f, 	9f/16f, 	0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(	0f, 	9f/16f, 	5f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(	5f/16f, 	9f/16f, 	0f);
	
	public static final int NODE_COUNT = 8;

	public static final long CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public AccumulatorTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, MAX_OUT);
		
		setLazyTickRate(20);
		
		connectionPos = new BlockPos[getNodeCount()];
		connectionIndecies = new int[getNodeCount()];
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
	public int getNodeFromPos(Vec3 vec) {
		Direction dir = level.getBlockState(worldPosition).getValue(AccumulatorBlock.FACING);
		boolean upper = true;
		vec = vec.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
		switch(dir) {
			case NORTH:
				upper = vec.z() < 0.5d;
				break;
			case WEST:
				upper = vec.x() < 0.5d;
				break;
			case SOUTH:
				upper = vec.z() > 0.5d;
				break;
			case EAST:
				upper = vec.x() > 0.5d;
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
		return connectionIndecies[node];
	}
	
	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {
		connectionPos[node] = pos; 
		connectionIndecies[node] = other;
		connectionTypes[node] = type;
		
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
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
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}
	
	@Override
	public int getNodeCount() {
		return NODE_COUNT;
	}

	@Override
	public BlockPos getMyPos() {
		return worldPosition;
	}

	@Override
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
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
	
	@Override
	public void tick() {
		super.tick();
		if(firstTickState)
			firstTick();
		firstTickState = false;
		if(level.isClientSide())
			return;
		networkTick();
	}
	
	private long demandOut = 0;
	private long demandIn = 0;
	private void networkTick() {
		if(awakeNetwork(level)) {
			//EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, 0);//EnergyNetwork.buildNetwork(world, this);
			causeBlockUpdate();
		}
		if(networkOut == null) {
			//System.out.println("WARN!");
			return;
		}
		
		try(Transaction t = Transaction.openOuter()) {
			long demand, pull;
			try(Transaction nested = Transaction.openNested(t)) {
				demand = networkOut.push(energy.extract(demandOut, nested));

				/*energy.receiveEnergy(networkOut.push(energy.extractEnergy(demandOut, false)), false);*/
				pull = networkIn.pull(Math.min(demandIn, energy.insert(MAX_IN, nested)));

				demandIn = networkIn.demand(energy.insert(MAX_IN, nested));
			}
			energy.extract(demand, t);

			demandOut = networkOut.getDemand();
			energy.insert(pull, t);
			t.commit();
		}
	}
	
	public void onBlockRemoved() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			if(node == null)
				break;
			int other = getOtherNodeIndex(i);
			node.removeNode(other);
			node.invalidateNodeCache();
			RemoveConnectorPacket.send(node.getMyPos(), other, level);
		}
		invalidateNodeCache();
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
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
	
	public void setEnergy(int energy) {
		this.energy.setEnergy(energy);
	}

	@Override
	public int getComparetorOverride() {
		return (int)((double)energy.getAmount() / (double)energy.getCapacity() * 15d);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		@SuppressWarnings("resource")
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray == null)
			return false;
		int node = getNodeFromPos(ray.getLocation());
		
		ObservePacket.send(worldPosition, node);
		
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" "))
				.append(Util.getTextComponent(energy)));
		
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
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(worldPosition, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
		causeBlockUpdate();
	}
}
