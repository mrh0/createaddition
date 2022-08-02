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

public class EnergyNetworkPacket {
	private BlockPos pos;
	private int demand;
	private int buff;
	
	public static double clientSaturation = 0;
	public static int clientDemand = 0;
	public static int clientBuff = 0;
	
	public EnergyNetworkPacket(BlockPos pos, int demand, int buff) {
		this.pos = pos;
		this.demand = demand;
		this.buff = buff;
	}
	
	public FriendlyByteBuf encode() {
		FriendlyByteBuf tag = PacketByteBufs.create();
        tag.writeBlockPos(pos);
        tag.writeInt(demand);
        tag.writeInt(buff);
		return tag;
    }
	
	public static EnergyNetworkPacket decode(FriendlyByteBuf buf) {
		EnergyNetworkPacket scp = new EnergyNetworkPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
        return scp;
    }
	
	public static void handle(EnergyNetworkPacket pkt, Minecraft client) {
		client.execute(() -> {
			try {
				updateClientCache(pkt.pos, pkt.demand, pkt.buff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static void updateClientCache(BlockPos pos, int demand, int buff) {
		clientDemand = demand;
		clientBuff = buff;
		clientSaturation = buff - demand;
    }
	
	public static void send(BlockPos pos, int demand, int buff, ServerPlayer player) {
		ServerPlayNetworking.send(player, CANetwork.ENERGY_NETWORK, new EnergyNetworkPacket(pos, demand, buff).encode());
	}
}
