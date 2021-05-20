package com.mrh0.createaddition.blocks.accumulator;


import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.IEnergyStorage;

public class AccumulatorTileEntity extends BaseElectricTileEntity implements IWireNode, IHaveGoggleInformation, IComparatorOverride {

	private final InternalEnergyStorage energyBufferIn;
	private final InternalEnergyStorage energyBufferOut;
	
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

		energyBufferIn = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		energyBufferOut = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		
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
			nodeCache[node] = IWireNode.getWireNode(world, getNodePos(node));
		if(nodeCache[node] == null)
			setNode(node, -1, null, null);
		
		return nodeCache[node];
	}
	
	@Override
	public Vector3f getNodeOffset(int node) {
		if(node > 3) {
			switch(getBlockState().get(AccumulatorBlock.FACING)) {
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
			switch(getBlockState().get(AccumulatorBlock.FACING)) {
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
	public IEnergyStorage getNodeEnergyStorage(int node) {
		return isNodeInput(node) ? energyBufferIn : energyBufferOut;
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
		Direction dir = world.getBlockState(pos).get(AccumulatorBlock.FACING);
		boolean upper = true;
		vec = vec.subtract(pos.getX(), pos.getY(), pos.getZ());
		switch(dir) {
			case NORTH:
				upper = vec.getZ() < 0.5d;
				break;
			case WEST:
				upper = vec.getX() < 0.5d;
				break;
			case SOUTH:
				upper = vec.getZ() > 0.5d;
				break;
			case EAST:
				upper = vec.getX() > 0.5d;
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
	public int getNodeIndex(int node) {
		if(connectionPos[node] == null)
			return -1;
		return connectionIndecies[node];
	}
	
	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {
		connectionPos[node] = pos; 
		connectionIndecies[node] = other;
		connectionTypes[node] = type;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT nbt, boolean clientPacket) {
		super.fromTag(state, nbt, clientPacket);
		for(int i = 0; i < getNodeCount(); i++)
			if(IWireNode.hasNode(nbt, i))
				readNode(nbt, i);
		energyBufferIn.read(nbt, "buffIn");
		energyBufferOut.read(nbt, "buffOut");
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
		energyBufferIn.write(nbt, "buffIn");
		energyBufferOut.write(nbt, "buffOut");
	}
	
	@Override
	public void removeNode(int other) {
		IWireNode.super.removeNode(other);
		invalidateNodeCache();
		this.markDirty();
	}
	
	@Override
	public int getNodeCount() {
		return 8;
	}

	@Override
	public BlockPos getMyPos() {
		return pos;
	}

	@Override
	public void setCache(Direction side, IEnergyStorage storage) {
	}

	@Override
	public IEnergyStorage getCachedEnergy(Direction side) {
		return null;
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

		int ext1 = energyBufferIn.extractEnergy(Integer.MAX_VALUE, false);
		energyBufferIn.receiveEnergy(ext1 - energy.receiveEnergy(ext1, false), false);
		
		int ext2 = energy.extractEnergy(Integer.MAX_VALUE, false);
		energy.receiveEnergy(ext2 - energyBufferOut.receiveEnergy(ext2, false), false);
		
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
		
		int comp = getComparetorOverride();
		if(comp != lastComparator)
			world.notifyNeighborsOfStateChange(pos, CABlocks.ACCUMULATOR.get());
		lastComparator = comp;
		
		if(energy.getEnergyStored() != lastEnergy)
			causeBlockUpdate();
		lastEnergy = energy.getEnergyStored();
	}
	
	@Override
	public void remove() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			if(node == null)
				break;
			node.removeNode(getNodeIndex(i));
			node.invalidateNodeCache();
		}
		invalidateNodeCache();
		invalidateCaps();
		super.remove();
	}
	
	public void setEnergy(int energy, int buffIn, int buffOut) {
		this.energy.setEnergy(energy);
		this.energyBufferIn.setEnergy(buffIn);
		this.energyBufferOut.setEnergy(buffOut);
	}

	@Override
	public int getComparetorOverride() {
		return (int)((double)energy.getEnergyStored() / (double)energy.getMaxEnergyStored() * 15d);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").formatted(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").formatted(TextFormatting.AQUA)));
		return true;
	}
}
