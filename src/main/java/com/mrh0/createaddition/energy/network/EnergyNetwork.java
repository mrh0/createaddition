package com.mrh0.createaddition.energy.network;

import java.util.Collections;
import java.util.Set;

import com.mrh0.createaddition.config.CommonConfig;

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

	// The graph ports this network spans; maintained by WireGraph.
	Set<PortKey> members = Collections.emptySet();

	public EnergyNetwork() {
		this.inBuff = 0;
		this.outBuff = 0;
		this.outBuffRetained = 0;
		this.inDemand = 0;
		this.outDemand = 0;
		this.valid = true;
	}

	public int getMaxBuff() {
		return Math.min(members.size() * (outDemand + inDemand * 2 + 10), CommonConfig.CONNECTOR_NETWORK_INTERNAL_BUFFER.get());
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

	// Takes over the stored energy of a network this one replaced.
	void absorb(EnergyNetwork other) {
		restore(other.inBuff, other.outBuff);
	}

	void restore(int in, int out) {
		inBuff += in;
		outBuff += out;
		outBuffRetained = outBuff;
	}

	boolean hasStoredEnergy() {
		return inBuff > 0 || outBuff > 0;
	}

	int getStoredIn() {
		return inBuff;
	}

	int getStoredOut() {
		return outBuff;
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
