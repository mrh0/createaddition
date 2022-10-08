package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementManager;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.network.ObservePacket;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GameEvents {
	@SubscribeEvent
	public static void worldTickEvent(TickEvent.LevelTickEvent evt) {
		if(evt.level.isClientSide())
			return;
		if(evt.phase == Phase.END)
			return;
		EnergyNetworkManager.tickWorld(evt.level);
		ConnectorMovementManager.tickWorld(evt.level);
	}
	
	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent evt) {
		if(evt.phase == Phase.START)
			return;
		ObservePacket.tick();
	}
	
	@SubscribeEvent
	public static void loadEvent(LevelEvent.Load evt) {
		if(evt.getLevel().isClientSide())
			return;
		new EnergyNetworkManager(evt.getLevel());
		new ConnectorMovementManager(evt.getLevel());
	}
}
