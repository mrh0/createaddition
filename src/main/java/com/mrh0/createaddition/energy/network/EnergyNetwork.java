package com.mrh0.createaddition.energy.network;

import java.util.Map;

import com.mrh0.createaddition.config.Config;
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

	private int nodeCount = 0;
	
	private static final int MAX_BUFF = 80000;
	
	public EnergyNetwork(Level world) {
		this.inBuff = 0;
		this.outBuff = 0;
		this.outBuffRetained = 0;
		this.inDemand = 0;
		this.outDemand = 0;
		this.valid = true;
		
		EnergyNetworkManager.instances.get(world).add(this);
	}

	public int getMaxBuff() {
		return Math.min(nodeCount * (outDemand + inDemand * 2 + 10), MAX_BUFF);
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

	// Returns the amount of energy pushed to network
	public int push(int energy, boolean simulate) {
		energy = Math.min(getMaxBuff() - inBuff, energy);
		energy = Math.max(energy, 0);
		if (!simulate) {
			inBuff += energy;
			pushed += energy;
		}
		return energy;
	}

	public int push(int energy) {
		return push(energy, false);
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

	// Returns amount of energy pulled from network
	public int pull(int energy, boolean simulate) {
		int r = Math.max(Math.min(energy, outBuff), 0);
		if (!simulate) {
			outBuff -= r;
			pulled += r;
		}
		return r;
	}

	public int pull(int max) {
		return pull(max, false);
	}
	
	public static EnergyNetwork nextNode(Level level, EnergyNetwork en, Map<String, IWireNode> visited, IWireNode current, int index) {
		if (visited.containsKey(posKey(current.getPos(), index))) return null; // should never matter?
		current.setNetwork(index, en);
		visited.put(posKey(current.getPos(), index), current);
		en.nodeCount++;
		
		for (int i = 0; i < current.getNodeCount(); i++) {
			IWireNode next = current.getWireNode(i);
			if (next == null) continue;
			if (!current.isNodeIndeciesConnected(index, i)) continue;
			nextNode(level, en, visited, next, current.getOtherNodeIndex(i));
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
	
	public void removed() {}
	
	public int getId() {
		return id;
	}
}
