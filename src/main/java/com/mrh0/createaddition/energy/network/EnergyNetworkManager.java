package com.mrh0.createaddition.energy.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.IWorld;

public class EnergyNetworkManager {
	public static Map<IWorld, EnergyNetworkManager> instances = new HashMap<>();
	
	private List<EnergyNetwork> networks;
	
	public EnergyNetworkManager(IWorld world) {
		instances.put(world, this);
		networks = new ArrayList<EnergyNetwork>();
	}
	
	public void add(EnergyNetwork network) {
		networks.add(network);
	}
	
	public void tick() {
		List<EnergyNetwork> keep = new ArrayList<EnergyNetwork>();
		for(int i = 0; i < networks.size(); i++) {
			EnergyNetwork en = networks.get(i);
			if(en.isValid()) {
				en.tick();
				keep.add(en);
				continue;
			}
			en.removed();
		}
		networks = keep;
	}
	
	public static void tickWorld(IWorld world) {
		instances.get(world).tick();
	}
}
