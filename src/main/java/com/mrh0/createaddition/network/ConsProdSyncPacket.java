package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ConsProdSyncPacket {
	private BlockPos pos;
	private int consumption;
	private int production;
	
	public static double clientConsumption = 0;
	public static int clientProduction = 0;
	
	public ConsProdSyncPacket(BlockPos pos, int consumption, int production) {
		this.pos = pos;
		this.consumption = consumption;
		this.production = production;
	}
	
	public FriendlyByteBuf encode() {
		FriendlyByteBuf tag = PacketByteBufs.create();
        tag.writeBlockPos(pos);
        tag.writeInt(consumption);
        tag.writeInt(production);
		return tag;
    }
	
	public static ConsProdSyncPacket decode(FriendlyByteBuf buf) {
		ConsProdSyncPacket scp = new ConsProdSyncPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
        return scp;
    }
	
	public static void handle(ConsProdSyncPacket pkt, Minecraft client) {
		client.execute(() -> {
			try {
				updateClientCache(pkt.pos, pkt.consumption, pkt.production);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static void updateClientCache(BlockPos pos, int consumption, int production) {
		clientConsumption = consumption;
		clientProduction = production;
    }
	
	public static void send(BlockPos pos, int consumption, int production, ServerPlayer player) {
		ServerPlayNetworking.send(player, CANetwork.CONS_PROD_SYNC, new ConsProdSyncPacket(pos, consumption, production).encode());
	}
}
