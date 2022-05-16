package com.mrh0.createaddition.network;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RemoveConnectorPacket {
	private BlockPos pos;
	private int node;
	
	public RemoveConnectorPacket(BlockPos pos, int node) {
		this.pos = pos;
		this.node = node;
	}
	
	public FriendlyByteBuf encode() {
		FriendlyByteBuf tag = PacketByteBufs.create();
        tag.writeBlockPos(this.pos);
        tag.writeInt(this.node);
		return tag;
    }
	
	public static RemoveConnectorPacket decode(FriendlyByteBuf buf) {
		RemoveConnectorPacket scp = new RemoveConnectorPacket(buf.readBlockPos(), buf.readInt());
        return scp;
    }
	
	public static void handle(RemoveConnectorPacket pkt, Minecraft client) {
		client.execute(() -> {
			try {
				
				handleData(pkt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static void handleData(RemoveConnectorPacket pkt) {
		BlockEntity te = (BlockEntity) ClientMinecraftWrapper.getClientLevel().getBlockEntity(pkt.pos);
        if (te != null) {
        	if(te instanceof IWireNode) {
        		IWireNode wn = (IWireNode) te;
        		wn.preformRemoveOfNode(pkt.node);
        	}
        }
    }
	
	public static void send(BlockPos pos, int node, Level level) {
		PlayerLookup.world((ServerLevel) level).forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer, CANetwork.REMOVE_CONNECTOR, new RemoveConnectorPacket(pos, node).encode()));
	}
}