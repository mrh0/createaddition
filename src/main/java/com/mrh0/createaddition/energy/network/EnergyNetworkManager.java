package com.mrh0.createaddition.energy.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.LevelAccessor;


public class EnergyNetworkManager {
	public static Map<LevelAccessor, EnergyNetworkManager> instances = new HashMap<>();
	
	private List<EnergyNetwork> networks;
	
	public EnergyNetworkManager(LevelAccessor world) {
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
				en.tick(i);
				keep.add(en);
				continue;
			}
			en.removed();
		}
		networks = keep;
	}
	
	public static void tickWorld(LevelAccessor world) {
		if(instances == null)
			return;
		if(instances.get(world) == null)
			return;
		instances.get(world).tick();
	}
}
