package com.mrh0.createaddition.energy.network;

import java.util.HashMap;
import java.util.Map;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IWireNode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public class EnergyNetwork {
	
	// Input
	private long inBuff;
	private long inDemand;
	// Output
	private long outBuff;
	private long outBuffRetained;
	private long outDemand;
	private boolean valid;
	
	private int pulled = 0;
	private int pushed = 0;
	
	private static long MAX_BUFF = 10000;//Math.max(Config.CONNECTOR_MAX_INPUT.get(), Config.CONNECTOR_MAX_OUTPUT.get());
	
	public EnergyNetwork(Level world) {
		this.inBuff = 0;
		this.outBuff = 0;
		this.outBuffRetained = 0;
		this.inDemand = 0;
		this.outDemand = 0;
		this.valid = true;
		
		
		EnergyNetworkManager.instances.get(world).add(this);
	}
	
	public void tick() {
		//System.out.println("NetTick: " + getBuff() + "/" + getDemand() + " " + pulled + "/" + pushed);
		long t = outBuff;
		outBuff = inBuff;
		outBuffRetained = outBuff;
		inBuff = t;
		outDemand = inDemand;
		inDemand = 0;
				
		pulled = 0;
		pushed = 0;
		//saturation = outDemand > 0 ? saturation : 0;
	}
	
	public long getBuff() {
		return outBuffRetained;
	}
	
	public long push(long energy) {
		energy = Math.min(MAX_BUFF - inBuff, energy);
		energy = energy > 0 ? energy : 0;
		inBuff += energy;
		pushed += energy;
		return energy;
	}
	
	public long demand(long demand) {
		this.inDemand += demand;
		return demand;
	}
	
	public long getDemand() {
		return outDemand;
	}
	
	public int getPulled() {
		return pulled;
	}
	
	public int getPushed() {
		return pushed;
	}
	
	public long pull(long max) {
		int r = (int) ( (double) Math.max(Math.min(max, outBuff), 0) );
		outBuff -= r;
		pulled += r;
		return r;
	}
	
	/*public static EnergyNetwork buildNetwork(World world, IWireNode root) {
		if(world.isRemote())
			return null;
		EnergyNetwork en = new EnergyNetwork(world);
		Map<String, IWireNode> visited = new HashMap<>();
		
		for(int i = 0; i < root.getNodeCount(); i++) {
			//if(!root.isNodeIndeciesConnected(i-1, i))
			//	en = new EnergyNetwork(world);
			//nextNode(world, en, visited, root, i);
			//System.out.println(root.getMyPos() + ":" + i);
		}
		return en;
	}*/
	
	/*public static EnergyNetwork buildNetwork(World world, IWireNode root, int index) {
		EnergyNetwork en = new EnergyNetwork(world);
		Map<String, IWireNode> visited = new HashMap<>();
		nextNode(world, en, visited, root, index);
		return en;
	}*/
	
	public static EnergyNetwork nextNode(Level world, EnergyNetwork en, Map<String, IWireNode> visited, IWireNode current, int index) {
		if(visited.containsKey(posKey(current.getMyPos(), index)))
			return null; // should never matter?
		current.setNetwork(index, en);
		visited.put(posKey(current.getMyPos(), index), current);
		
		for(int i = 0; i < current.getNodeCount(); i++) {
			IWireNode next = current.getNode(i);
			if(next == null)
				continue;
			if(!current.isNodeIndeciesConnected(index, i)) {
				/*if(current.getNetwork(i) == null) {
					nextNode(world, new EnergyNetwork(world), new HashMap<String, IWireNode>(), current, i);
					System.out.println(current.getMyPos() + ":" + i);
				}*/
				continue;
			}
			nextNode(world, en, visited, next, current.getOtherNodeIndex(i));
		}
		return en;
	}
	
	private static String posKey(BlockPos pos, int index) {
		return pos.getX()+","+pos.getY()+","+pos.getZ()+":"+index;
	}
	
	public void invalidate() {
		this.valid = false;
	}
	
	public boolean isValid() {
		return this.valid;
	}
	
	public void removed() {
		
	}
}
