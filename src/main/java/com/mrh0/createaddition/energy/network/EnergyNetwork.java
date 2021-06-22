package com.mrh0.createaddition.energy.network;

import java.util.HashMap;
import java.util.Map;

import com.mrh0.createaddition.energy.IWireNode;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetwork {
	
	// Input
	private int inBuff;
	private int inDemand;
	// Output
	private int outBuff;
	private int outDemand;
	private int nodes;
	private boolean valid;
	private double saturation;
	
	public EnergyNetwork(World world) {
		this.inBuff = 0;
		this.outBuff = 0;
		this.inDemand = 0;
		this.outDemand = 0;
		this.nodes = 0;
		this.valid = true;
		EnergyNetworkManager.instances.get(world).add(this);
	}
	
	public void tick() {
		//System.out.println("NetTick: " + inBuff + "/" + outBuff + " | " + inDemand + "/" + outDemand + " | " + saturation);
		saturation = (double)inBuff - (double)outDemand;
		outBuff = inBuff;
		inBuff = 0;
		outDemand = inDemand;
		inDemand = 0;
		
		//saturation = outDemand > 0 ? saturation : 0;
	}
	
	public int getBuff() {
		return outBuff;
	}
	
	public int push(int energy) {
		energy = Math.min(outDemand - outBuff, energy);
		energy = energy > 0 ? energy : 0;
		inBuff += energy;
		return energy;
	}
	
	public int demand(int demand) {
		this.inDemand += demand;
		return demand;
	}
	
	public int getDemand() {
		return outDemand;
	}
	
	public int pull(int max) {
		int r = (int) ( (double) Math.min(max, outBuff) );
		outBuff -= r;
		return r;
	}
	
	public static EnergyNetwork buildNetwork(World world, IWireNode root) {
		if(world.isRemote())
			return null;
		EnergyNetwork en = new EnergyNetwork(world);
		Map<String, IWireNode> visited = new HashMap<>();
		for(int i = 0; i < root.getNodeCount(); i++) {
			if(!root.isNodeIndeciesConnected(i-1, i))
				en = new EnergyNetwork(world);
			nextNode(world, en, visited, root, i);
		}
		return en;
	}
	
	public static EnergyNetwork buildNetwork(World world, IWireNode root, int index) {
		EnergyNetwork en = new EnergyNetwork(world);
		Map<String, IWireNode> visited = new HashMap<>();
		nextNode(world, en, visited, root, index);
		return en;
	}
	
	public static void nextNode(World world, EnergyNetwork en, Map<String, IWireNode> visited, IWireNode current, int index) {
		en.nodes++;
		if(visited.containsKey(posKey(current.getMyPos(), index)))
			return;
		current.setNetwork(index, en);
		visited.put(posKey(current.getMyPos(), index), current);
		for(int i = 0; i < current.getNodeCount(); i++) {
			IWireNode next = current.getNode(i);
			if(next == null)
				continue;
			if(!current.isNodeIndeciesConnected(index, i)) {
				if(current.getNetwork(i) == null)
					nextNode(world, new EnergyNetwork(world), new HashMap<String, IWireNode>(), current, i);
				continue;
			}
			nextNode(world, en, visited, next, current.getOtherNodeIndex(i));
		}
		en.nodes++;
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
	
	public double getSaturation() {
		return saturation;
	}
}
