package com.mrh0.createaddition.energy.network;

public class EnergyNetwork {
	
	private int inBuff;
	private int outBuff;
	private int demand;
	private int nodes;
	
	public EnergyNetwork() {
		this.inBuff = 0;
		this.outBuff = 0;
		this.demand = 0;
		this.nodes = 0;
	}
	
	public void tick() {
		outBuff = inBuff;
		inBuff = 0;
		demand = 0;
	}
	
	public int pool() {
		return outBuff;
	}
	
	public void addNode() {
		nodes++;
	}
	
	public void push(int energy) {
		inBuff += energy;
	}
	
	public void addDemand(int demand) {
		this.demand += demand;
	}
	
	public int pull() {
		return Math.max(0, pool());
	}
}
