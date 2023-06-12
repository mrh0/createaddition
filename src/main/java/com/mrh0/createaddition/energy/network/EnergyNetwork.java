package com.mrh0.createaddition.energy.network;

import java.util.Map;

import com.mrh0.createaddition.energy.IWireNode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EnergyNetwork {
	private int id;
	// Input
	private int inBuff;
	private int inDemand;
	// Output
	private int outBuff;
	private int outBuffRetained;
	private int outDemand;
	private boolean valid;
	
	private int pulled = 0;
	private int pushed = 0;
	
	private static int MAX_BUFF = 10000;
	
	public EnergyNetwork(Level world) {
		this.inBuff = 0;
		this.outBuff = 0;
		this.outBuffRetained = 0;
		this.inDemand = 0;
		this.outDemand = 0;
		this.valid = true;
		
		
		EnergyNetworkManager.instances.get(world).add(this);
	}
	
	public void tick(int index) {
		this.id = index;
		int t = outBuff;
		outBuff = inBuff;
		outBuffRetained = outBuff;
		inBuff = t;
		outDemand = inDemand;
		inDemand = 0;
				
		pulled = 0;
		pushed = 0;
	}
	
	public int getBuff() {
		return outBuffRetained;
	}

	// Returns the amount of energy pushed
	public int push(int energy) {
		energy = Math.min(MAX_BUFF - inBuff, energy);
		energy = Math.max(energy, 0);
		inBuff += energy;
		pushed += energy;
		return energy;
	}
	
	public int demand(int demand) {
		this.inDemand += demand;
		return demand;
	}
	
	public int getDemand() {
		return outDemand;
	}
	
	public int getPulled() {
		return pulled;
	}
	
	public int getPushed() {
		return pushed;
	}
	
	public int pull(int max) {
		int r = (int) ( (double) Math.max(Math.min(max, outBuff), 0) );
		outBuff -= r;
		pulled += r;
		return r;
	}
	
	public static EnergyNetwork nextNode(Level world, EnergyNetwork en, Map<String, IWireNode> visited, IWireNode current, int index) {
		if(visited.containsKey(posKey(current.getPos(), index)))
			return null; // should never matter?
		current.setNetwork(index, en);
		visited.put(posKey(current.getPos(), index), current);
		
		for(int i = 0; i < current.getNodeCount(); i++) {
			IWireNode next = current.getWireNode(i);
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
	
	public int getId() {
		return id;
	}
}
