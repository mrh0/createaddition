package com.mrh0.createaddition.network;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.Executor;

public class CANetwork {
    public static final ResourceLocation CONS_PROD_SYNC = new ResourceLocation(CreateAddition.MODID, "con_prod_sync_packet");
    public static final ResourceLocation ENERGY_NETWORK = new ResourceLocation(CreateAddition.MODID, "energy_network_packet");
    public static final ResourceLocation OBSERVE_PACKET = new ResourceLocation(CreateAddition.MODID, "observe_packet");

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(OBSERVE_PACKET, (server, player, handler, buf, responseSender) -> {
           ObservePacket.handle(ObservePacket.decode(buf), server, player);
        });
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(ENERGY_NETWORK, (client, handler, buf, responseSender) -> {
            EnergyNetworkPacket.handle(EnergyNetworkPacket.decode(buf), client);
        });
        ClientPlayNetworking.registerGlobalReceiver(CONS_PROD_SYNC, (client, handler, buf, responseSender) -> {
            ConsProdSyncPacket.handle(ConsProdSyncPacket.decode(buf), client);
        });
    }
}
