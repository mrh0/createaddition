package com.mrh0.createaddition.energy.network;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.utility.Pair;

public class EnergyNetworkManager {
	private List<EnergyNetwork> networks;
	
	public EnergyNetworkManager() {
		networks = new ArrayList<EnergyNetwork>();
	}
	
	public void add(EnergyNetwork network) {
		networks.add(network);
	}
	
	public void merge(EnergyNetwork n1, EnergyNetwork n2) {
		
	}
	
	public Pair<EnergyNetwork, EnergyNetwork> split() {
		return null;
	}
}
