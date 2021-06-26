package com.mrh0.createaddition.blocks.redstone_relay;

import java.util.HashMap;
import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

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

public class RedstoneRelayTileEntity extends SmartTileEntity implements IWireNode, IHaveGoggleInformation, IObserveTileEntity {

	//private final InternalEnergyStorage energyBufferIn;
	//private final InternalEnergyStorage energyBufferOut;
	
	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vector3f OFFSET_NORTH = new Vector3f(	0f, 	-1f/16f, 	-5f/16f);
	public static Vector3f OFFSET_WEST = new Vector3f(	-5f/16f, 	-1f/16f, 	0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(	0f, 	-1f/16f, 	5f/16f);
	public static Vector3f OFFSET_EAST = new Vector3f(	5f/16f, 	-1f/16f, 	0f);
	
	public static Vector3f IN_VERTICAL_OFFSET_NORTH = new Vector3f(	5f/16f, 	0f, 	-1f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_WEST = new Vector3f(	-1f/16f, 	0f, 	-5f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_SOUTH = new Vector3f(	-5f/16f, 	0f, 	1f/16f);
	public static Vector3f IN_VERTICAL_OFFSET_EAST = new Vector3f(	1f/16f, 	0f, 	5f/16f);
	
	public static Vector3f OUT_VERTICAL_OFFSET_NORTH = new Vector3f(	-5f/16f, 	0f, 	-1f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_WEST = new Vector3f(	-1f/16f, 	0f, 	5f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_SOUTH = new Vector3f(	5f/16f, 	0f, 	1f/16f);
	public static Vector3f OUT_VERTICAL_OFFSET_EAST = new Vector3f(	1f/16f, 	0f, 	-5f/16f);
	
	public static final int CAPACITY = Config.ACCUMULATOR_CAPACITY.get(), MAX_IN = Config.ACCUMULATOR_MAX_INPUT.get(), MAX_OUT = Config.ACCUMULATOR_MAX_OUTPUT.get();
	
	public RedstoneRelayTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);

		//energyBufferIn = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		//energyBufferOut = new InternalEnergyStorage(ConnectorTileEntity.CAPACITY, MAX_IN, MAX_OUT);
		
		//setLazyTickRate(20);
		
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
		boolean vertical = getBlockState().get(RedstoneRelay.VERTICAL);
		Direction direction = getBlockState().get(RedstoneRelay.HORIZONTAL_FACING);
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
	public int getNodeFromPos(Vector3d vec) {
		Direction dir = world.getBlockState(pos).get(RedstoneRelay.HORIZONTAL_FACING);
		boolean vertical = world.getBlockState(pos).get(RedstoneRelay.VERTICAL);
		boolean upper = true;
		vec = vec.subtract(pos.getX(), pos.getY(), pos.getZ());
		if(vertical) {
			switch(dir) {
			case NORTH:
				upper = vec.getX() < 0.5d;
				break;
			case WEST:
				upper = vec.getZ() > 0.5d;
				break;
			case SOUTH:
				upper = vec.getX() > 0.5d;
				break;
			case EAST:
				upper = vec.getZ() < 0.5d;
				break;
			default:
				break;
			}
		}
		else {
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
			else
				writeNode(nbt, i);
		}
	}
	
	@Override
	public void removeNode(int other) {
		IWireNode.super.removeNode(other);
		invalidateNodeCache();
		this.markDirty();
		
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
		return pos;
	}

	@Override
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(world.isRemote())
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
		if(awakeNetwork(world)) {
			//EnergyNetwork.nextNode(world, new EnergyNetwork(world), new HashMap<>(), this, 0);//EnergyNetwork.buildNetwork(world, this);
			causeBlockUpdate();
		}
		BlockState bs = getBlockState();
		if(!bs.isIn(CABlocks.REDSTONE_RELAY.get()))
			return;
		if(bs.get(RedstoneRelay.POWERED)) {
			networkOut.push(networkIn.pull(demand));
			demand = networkIn.demand(networkOut.getDemand());
		}
	}
	
	@Override
	public void remove() {
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
		// Invalidate
		if(networkIn != null)
			networkIn.invalidate();
		if(networkOut != null)
			networkOut.invalidate();
		super.remove();
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
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		@SuppressWarnings("resource")
		RayTraceResult ray = Minecraft.getInstance().objectMouseOver;
		if(ray == null)
			return false;
		int node = getNodeFromPos(ray.getHitVec());
		
		ObservePacket.send(pos, node);
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.relay.info").formatted(TextFormatting.WHITE)));
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.selected").formatted(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" "))
				.append(new TranslationTextComponent(isNodeInput(node) ? "createaddition.tooltip.energy.input" : "createaddition.tooltip.energy.output").formatted(TextFormatting.AQUA)));
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.usage").formatted(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(" ")
				.append(Multimeter.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").formatted(TextFormatting.AQUA));
		
		return true;
	}

	@Override
	public void onObserved(ServerPlayerEntity player, ObservePacket pack) {
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(pos, getNetwork(pack.getNode()).getPulled(), getNetwork(pack.getNode()).getPushed(), player);
	}
}
