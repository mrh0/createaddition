package com.mrh0.createaddition.blocks.accumulator;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelay;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.IEnergyStorage;

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode, IHaveGoggleInformation, IComparatorOverride, IObserveTileEntity {
	
	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vector3f OFFSET_NORTH = new Vector3f(	0f, 	9f/16f, 	-5f/16f);
	public static Vector3f OFFSET_WEST = new Vector3f(	-5f/16f, 	9f/16f, 	0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(	0f, 	9f/16f, 	5f/16f);
	public static Vector3f OFFSET_EAST = new Vector3f(	5f/16f, 	9f/16f, 	0f);
	
	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public AccumulatorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);
		
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
	public Vector3f getNodeOffset(int node) {
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
	public int getNodeFromPos(Vector3d vec) {
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
	public void fromTag(BlockState state, CompoundNBT nbt, boolean clientPacket) {
		super.fromTag(state, nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++)
			if(IWireNode.hasNode(nbt, i))
				readNode(nbt, i);
	}
	
	@Override
	public void write(CompoundNBT nbt, boolean clientPacket) {
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
		return 8;
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
		
		
		energy.extractEnergy(networkOut.push(energy.extractEnergy(demandOut, true)), false);
		
		/*energy.receiveEnergy(networkOut.push(energy.extractEnergy(demandOut, false)), false);*/
		demandOut = networkOut.getDemand();
		energy.receiveEnergy(networkIn.pull(Math.min(demandIn, energy.receiveEnergy(MAX_IN, true))), false);
		demandIn = networkIn.demand(energy.receiveEnergy(MAX_IN, true));
		
	}
	
	@Override
	public void setRemoved() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			if(node == null)
				break;
			node.removeNode(getOtherNodeIndex(i));
			node.invalidateNodeCache();
		}
		invalidateNodeCache();
		invalidateCaps();
		super.setRemoved();
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
		super.setRemoved();
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
		return (int)((double)energy.getEnergyStored() / (double)energy.getMaxEnergyStored() * 15d);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		
		@SuppressWarnings("resource")
		RayTraceResult ray = Minecraft.getInstance().hitResult;
		if(ray == null)
			return false;
		int node = getNodeFromPos(ray.getLocation());
		
		ObservePacket.send(worldPosition, node);
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.accumulator.info").withStyle(TextFormatting.WHITE)));
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" "))
				.append(Multimeter.getTextComponent(energy)));
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.selected").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" "))
				.append(new TranslationTextComponent(isNodeInput(node) ? "createaddition.tooltip.energy.input" : "createaddition.tooltip.energy.output").withStyle(TextFormatting.AQUA)));
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(" ")
				.append(Multimeter.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(TextFormatting.AQUA));
		
		return true;
	}

	@Override
	public void onObserved(ServerPlayerEntity player, ObservePacket pack) {
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(worldPosition, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
		causeBlockUpdate();
	}
}
