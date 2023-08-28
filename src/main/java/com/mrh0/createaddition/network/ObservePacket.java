package com.mrh0.createaddition.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ObservePacket(BlockPos pos, int node) {

	public FriendlyByteBuf encode() {
		FriendlyByteBuf tag = PacketByteBufs.create();
		tag.writeBlockPos(pos);
		tag.writeInt(node);
		return tag;
	}

	public static ObservePacket decode(FriendlyByteBuf buf) {
		return new ObservePacket(buf.readBlockPos(), buf.readInt());
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
		BlockEntity te = player.level().getBlockEntity(pkt.pos);
		if (te != null) {
			if (te instanceof IObserveTileEntity ote) {
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
		if (cooldown < 0)
			cooldown = 0;
	}

	public static boolean send(BlockPos pos, int node) {
		if (cooldown > 0)
			return false;
		cooldown = 10;
		ClientPlayNetworking.send(CANetwork.OBSERVE_PACKET, new ObservePacket(pos, node).encode());
		return true;
	}


}

