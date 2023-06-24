package com.mrh0.createaddition.energy.network;

import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EnergyNetworkManager {
	public static Map<LevelAccessor, EnergyNetworkManager> instances = new HashMap<>();
	
	private List<EnergyNetwork> networks;
	
	public EnergyNetworkManager(LevelAccessor world) {
		instances.put(world, this);
		networks = new ArrayList<>();
	}
	
	public void add(EnergyNetwork network) {
		networks.add(network);
	}
	
	public void tick() {
		List<EnergyNetwork> keep = new ArrayList<>();
		for (EnergyNetwork en : networks) {
			if (en.isValid()) {
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
