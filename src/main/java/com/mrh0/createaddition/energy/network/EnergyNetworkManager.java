package com.mrh0.createaddition.energy.network;

import java.util.*;

import net.minecraft.world.level.LevelAccessor;


public class EnergyNetworkManager {
	public static Map<LevelAccessor, EnergyNetworkManager> instances = new WeakHashMap<>();
	
	private List<EnergyNetwork> networks;
	
	public EnergyNetworkManager(LevelAccessor world) {
		instances.put(world, this);
		networks = new ArrayList<EnergyNetwork>();
	}
	
	public void add(EnergyNetwork network) {
		networks.add(network);
	}
	
	public void tick() {
		Iterator<EnergyNetwork> it = networks.iterator();
		int i = 0;
		while (it.hasNext()) {
			EnergyNetwork en = it.next();
			if (en.isValid()) en.tick(i++);
			else {
				it.remove();
				en.removed();
			}
		}
	}
	
	public static void tickWorld(LevelAccessor world) {
		if(instances == null) return;
		if(instances.get(world) == null) return;
		instances.get(world).tick();
	}
}
