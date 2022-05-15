package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementManager;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.network.ObservePacket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class GameEvents {

	public static void initCommon() {
		ServerTickEvents.START_WORLD_TICK.register(GameEvents::worldTickEvent);
		ServerWorldEvents.LOAD.register(GameEvents::loadEvent);
		ConnectorMovementManager.tickWorld(evt.world);
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientTickEvents.END_CLIENT_TICK.register(GameEvents::clientTickEvent);
	}

	public static void worldTickEvent(ServerLevel world) {
		EnergyNetworkManager.tickWorld(world);
	}

	@Environment(EnvType.CLIENT)
	public static void clientTickEvent(Minecraft client) {
		ObservePacket.tick();
	}

	public static void loadEvent(MinecraftServer server, ServerLevel level) {
		new EnergyNetworkManager(level);
		new ConnectorMovementManager(evt.getWorld());
	}
}
