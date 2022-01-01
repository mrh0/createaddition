package com.mrh0.createaddition.blocks.connector;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.energy.network.EnergyNetwork;
import com.mrh0.createaddition.item.Multimeter;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
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

public class ConnectorTileEntity extends BaseElectricTileEntity implements IWireNode, IObserveTileEntity, IHaveGoggleInformation {

	private BlockPos[] connectionPos;
	private int[] connectionIndecies;
	private WireType[] connectionTypes;
	public IWireNode[] nodeCache;
	
	public static Vector3f OFFSET_DOWN = new Vector3f(0f, -3f/16f, 0f);
	public static Vector3f OFFSET_UP = new Vector3f(0f, 3f/16f, 0f);
	public static Vector3f OFFSET_NORTH = new Vector3f(0f, 0f, -3f/16f);
	public static Vector3f OFFSET_WEST = new Vector3f(-3f/16f, 0f, 0f);
	public static Vector3f OFFSET_SOUTH = new Vector3f(0f, 0f, 3f/16f);
	public static Vector3f OFFSET_EAST = new Vector3f(3f/16f, 0f, 0f);
	
	public static final int CAPACITY = Config.CONNECTOR_CAPACITY.get(), MAX_IN = Config.CONNECTOR_MAX_INPUT.get(), MAX_OUT = Config.CONNECTOR_MAX_OUTPUT.get();
	
	public ConnectorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);
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
			nodeCache[node] = IWireNode.getWireNode(level, getNodePos(node));
		if(nodeCache[node] == null)
			setNode(node, -1, null, null);
		
		return nodeCache[node];
	}

	@Override
	public Vector3f getNodeOffset(int node) {
		switch(getBlockState().getValue(ConnectorBlock.FACING)) {
			case DOWN:
				return OFFSET_DOWN;
			case UP:
				return OFFSET_UP;
			case NORTH:
				return OFFSET_NORTH;
			case WEST:
				return OFFSET_WEST;
			case SOUTH:
				return OFFSET_SOUTH;
			case EAST:
				return OFFSET_EAST;
		}
		return OFFSET_DOWN;
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
		return 4;
	}
	
	@Override
	public int getNodeFromPos(Vector3d vector3d) {
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
		return connectionIndecies[node];
	}
	
	@Override
	public void setNode(int node, int other, BlockPos pos, WireType type) {
		connectionPos[node] = pos; 
		connectionIndecies[node] = other;
		connectionTypes[node] = type;
		
		// Invalidate
		if(network != null)
			network.invalidate();
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
		if(network != null)
			network.invalidate();
	}
	
	/*@Override
	public void lazyTick() {
		super.lazyTick();
		
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
			
			int ext = energy.getEnergyStored()-es.getEnergyStored();
			ext = energy.extractEnergy(ext, false);
			es.receiveEnergy(Math.max(ext, 0), false);
		}
		
		Direction d = getBlockState().get(ConnectorBlock.FACING);
		TileEntity te = world.getTileEntity(pos.offset(d));
		if(te == null)
			return;
		LazyOptional<IEnergyStorage> opt = te.getCapability(CapabilityEnergy.ENERGY, d.getOpposite());
		IEnergyStorage ies = opt.orElse(null);
		if(ies == null)
			return;
		int ext = energy.extractEnergy(ies.receiveEnergy(MAX_OUT, true), false);
		ies.receiveEnergy(ext, false);
	}*/

	@Override
	public BlockPos getMyPos() {
		return worldPosition;
	}
	
	@Override
	public void setRemoved() {
		for(int i = 0; i < getNodeCount(); i++) {
			if(getNodeType(i) == null)
				continue;
			IWireNode node = getNode(i);
			node.removeNode(getOtherNodeIndex(i));
			node.invalidateNodeCache();
		}
		invalidateNodeCache();
		invalidateCaps();
		// Invalidate
		if(network != null)
			network.invalidate();
		super.setRemoved();
	}
	
	public void invalidateNodeCache() {
		for(int i = 0; i < getNodeCount(); i++)
			nodeCache[i] = null;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide())
			return;
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
	
	private int demand = 0;
	private void networkTick(EnergyNetwork en) {
		if(level.isClientSide())
			return;
		// TODO: Cache
		Direction d = getBlockState().getValue(ConnectorBlock.FACING);
		//TileEntity te = world.getTileEntity(pos.offset(d));
		//if(te == null)
		//	return;
		//IEnergyStorage ies = te.getCapability(CapabilityEnergy.ENERGY, d.getOpposite()).orElse(null);
		IEnergyStorage ies = getCachedEnergy(d);
		if(ies == null)
			return;
		
		int pull = en.pull(demand);
		ies.receiveEnergy(pull, false);
		
		int testExtract = energy.extractEnergy(Integer.MAX_VALUE, true);
		int testInsert = ies.receiveEnergy(MAX_OUT, true);
		
		demand = en.demand(testInsert);
		
		
		int push = en.push(testExtract);
		int ext = energy.internalConsumeEnergy(push);
	}

	@Override
	public void onObserved(ServerPlayerEntity player, ObservePacket pack) {
		if(isNetworkValid(0))
			EnergyNetworkPacket.send(worldPosition, getNetwork(0).getPulled(), getNetwork(0).getPushed(), player);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		ObservePacket.send(worldPosition, 0);
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.connector.info").withStyle(TextFormatting.WHITE)));
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(" ")
				.append(Multimeter.format((int)EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(TextFormatting.AQUA));
		
		/*tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.demand").formatted(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(" ")
				.append(Multimeter.format((int)EnergyNetworkPacket.clientDemand)).append("fe/t").formatted(TextFormatting.AQUA));*/
		
		
		/*tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.saturation").formatted(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + (EnergyNetworkPacket.clientSaturation > 0 ? "+" : "")))
				.append(Multimeter.format((int)EnergyNetworkPacket.clientSaturation)).append("fe/t").formatted(TextFormatting.AQUA));*/
		
		return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}
}
