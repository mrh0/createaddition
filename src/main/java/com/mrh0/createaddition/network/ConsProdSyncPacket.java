package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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
	
	public static void encode(ConsProdSyncPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.consumption);
        tag.writeInt(packet.production);
    }
	
	public static ConsProdSyncPacket decode(FriendlyByteBuf buf) {
		ConsProdSyncPacket scp = new ConsProdSyncPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
        return scp;
    }
	
	public static void handle(ConsProdSyncPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				updateClientCache(pkt.pos, pkt.consumption, pkt.production);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private static void updateClientCache(BlockPos pos, int consumption, int production) {
		clientConsumption = consumption;
		clientProduction = production;
    }
	
	public static void send(BlockPos pos, int consumption, int production, ServerPlayer player) {
		CreateAddition.Network.send(PacketDistributor.PLAYER.with(() -> player), new ConsProdSyncPacket(pos, consumption, production));
	}
}
