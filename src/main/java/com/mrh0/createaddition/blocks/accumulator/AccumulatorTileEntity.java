package com.mrh0.createaddition.blocks.accumulator;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
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
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

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

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode, IHaveGoggleInformation, IComparatorOverride, IObserveTileEntity {

	private final LocalNode[] localNodes;
	private final IWireNode[] _nodeCache;
	
	public static Vec3 OFFSET_NORTH = new Vec3(	0f, 	9f/16f, 	-5f/16f);
	public static Vec3 OFFSET_WEST = new Vec3(	-5f/16f, 	9f/16f, 	0f);
	public static Vec3 OFFSET_SOUTH = new Vec3(	0f, 	9f/16f, 	5f/16f);
	public static Vec3 OFFSET_EAST = new Vec3(	5f/16f, 	9f/16f, 	0f);
	
	public static final int NODE_COUNT = 8;
	
	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public AccumulatorTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, MAX_OUT);
		
		setLazyTickRate(20);

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
		
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
	}

	@Override
	public void removeNode(int index) {
		this.localNodes[index] = null;

		this.invalidateNodeCache();
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
		return NODE_COUNT;
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
	
	private int demandOut = 0;
	private int demandIn = 0;
	private void networkTick() {
		if(awakeNetwork(level)) {
			//EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, 0);//EnergyNetwork.buildNetwork(world, this);
			causeBlockUpdate();
		}
		if(networkOut == null) {
			//System.out.println("WARN!");
			return;
		}
		
		
		localEnergy.extractEnergy(networkOut.push(localEnergy.extractEnergy(demandOut, true)), false);
		
		/*energy.receiveEnergy(networkOut.push(energy.extractEnergy(demandOut, false)), false);*/
		demandOut = networkOut.getDemand();
		localEnergy.receiveEnergy(networkIn.pull(Math.min(demandIn, localEnergy.receiveEnergy(MAX_IN, true))), false);
		demandIn = networkIn.demand(localEnergy.receiveEnergy(MAX_IN, true));
		
	}
	
	public void onBlockRemoved() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getWireNode(i);
			if(node == null)
				break;
			int other = getOtherNodeIndex(i);
			node.removeNode(other);
			RemoveConnectorPacket.send(node.getPos(), other, level);
		}
		invalidateNodeCache();
		invalidateCaps();
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
		
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(ChatFormatting.WHITE)));
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" "))
				.append(Util.getTextComponent(localEnergy)));
		
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
