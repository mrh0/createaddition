package com.mrh0.createaddition.blocks.connector;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class ConnectorMovementManager {
	public static Map<LevelAccessor, ConnectorMovementManager> instances = new HashMap<>();
	
	private boolean changed = true;
	private Map<BlockPos, Void> map;
	
	public ConnectorMovementManager(LevelAccessor world) {
		instances.put(world, this);
		map = new HashMap<>();
	}
	
	public void setUpdated(BlockPos pos) {
		map.put(pos, null);
		changed = true;
	}
	
	public boolean isUpdated(BlockPos pos) {
		return map.containsKey(pos);
	}
	
	public void tick() {
		if(changed)
			map.clear();
		changed = false;
	}
	
	public static void setUpdated(LevelAccessor world, BlockPos pos) {
		if(instances == null) {
			System.err.println("WORLD DID NOT EXIST");
			return;
		}
		if(!instances.containsKey(world))
			return;
		instances.get(world).setUpdated(pos);
	}
	
	public static boolean isUpdated(LevelAccessor world, BlockPos pos) {
		if(instances == null) {
			System.err.println("WORLD DID NOT EXIST");
			return false;
		}
		if(!instances.containsKey(world))
			return false;
		return instances.get(world).isUpdated(pos);
	}
	
	public static void tickWorld(LevelAccessor world) {
		if(instances == null)
			return;
		if(!instances.containsKey(world))
			return;
		instances.get(world).tick();
	}
}
