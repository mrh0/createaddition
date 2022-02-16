package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ObservePacket {
	private BlockPos pos;
	private int node;
	
	public ObservePacket(BlockPos pos, int node) {
		this.pos = pos;
		this.node = node;
	}
	
	public FriendlyByteBuf encode() {
		FriendlyByteBuf tag = PacketByteBufs.create();
        tag.writeBlockPos(pos);
        tag.writeInt(node);
		return tag;
    }
	
	public static ObservePacket decode(FriendlyByteBuf buf) {
		ObservePacket scp = new ObservePacket(buf.readBlockPos(), buf.readInt());
        return scp;
    }
	
	public static void handle(ObservePacket pkt, MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			try {
				if (player != null) {
					sendUpdate(pkt, player);
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static void sendUpdate(ObservePacket pkt, ServerPlayer player) {
		BlockEntity te = (BlockEntity) player.level.getBlockEntity(pkt.pos);
        if (te != null) {
        	if(te instanceof IObserveTileEntity) {
	        	IObserveTileEntity ote = (IObserveTileEntity) te;
	        	ote.onObserved(player, pkt);
	            Packet<ClientGamePacketListener> supdatetileentitypacket = te.getUpdatePacket();
	            if (supdatetileentitypacket != null)
	                player.connection.send(supdatetileentitypacket);
        	}
        }
    }
	
	private static int cooldown = 0;
	public static void tick() {
		cooldown--;
		if(cooldown < 0)
			cooldown = 0;
	}
	
	public static void send(BlockPos pos, int node) {
		if(cooldown > 0)
			return;
		cooldown = 10;
		ClientPlayNetworking.send(CANetwork.OBSERVE_PACKET, new ObservePacket(pos, node).encode());
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public int getNode() {
		return node;
	}
}

