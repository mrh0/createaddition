package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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
	
	public static void encode(ConsProdSyncPacket packet, PacketBuffer tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.consumption);
        tag.writeInt(packet.production);
    }
	
	public static ConsProdSyncPacket decode(PacketBuffer buf) {
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
	
	public static void send(BlockPos pos, int consumption, int production, ServerPlayerEntity player) {
		CreateAddition.Network.send(PacketDistributor.PLAYER.with(() -> player), new ConsProdSyncPacket(pos, consumption, production));
	}
}
