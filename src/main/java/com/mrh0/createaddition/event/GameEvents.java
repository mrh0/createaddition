package com.mrh0.createaddition.event;

import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.network.ObservePacket;

import net.minecraft.potion.Effect;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GameEvents {
	@SubscribeEvent
	public static void worldTickEvent(TickEvent.WorldTickEvent evt) {
		if(evt.world.isClientSide())
			return;
		if(evt.phase == Phase.END)
			return;
		EnergyNetworkManager.tickWorld(evt.world);
	}
	
	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent evt) {
		if(evt.phase == Phase.START)
			return;
		ObservePacket.tick();
	}
	
	@SubscribeEvent
	public static void loadEvent(WorldEvent.Load evt) {
		if(evt.getWorld().isClientSide())
			return;
		new EnergyNetworkManager(evt.getWorld());
	}
}
