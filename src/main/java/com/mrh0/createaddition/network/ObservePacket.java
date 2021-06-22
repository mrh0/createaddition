package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ObservePacket {
	private BlockPos pos;
	
	public ObservePacket(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode(ObservePacket packet, PacketBuffer tag) {
        tag.writeBlockPos(packet.pos);
    }
	
	public static ObservePacket decode(PacketBuffer buf) {
		ObservePacket scp = new ObservePacket(buf.readBlockPos());
        return scp;
    }
	
	public static void handle(ObservePacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				ServerPlayerEntity player = ctx.get().getSender();
				
				if (player != null) {
					sendUpdate(pkt.pos, player);
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private static void sendUpdate(BlockPos pos, ServerPlayerEntity player) {
		TileEntity te = (TileEntity) player.world.getTileEntity(pos);
		IObserveTileEntity ote = (IObserveTileEntity) te;
        if (te != null) {
        	ote.onObserved(player);
            SUpdateTileEntityPacket supdatetileentitypacket = te.getUpdatePacket();
            if (supdatetileentitypacket != null) {
                player.connection.sendPacket(supdatetileentitypacket);
            }
        }
    }
	
	private static int cooldown = 0;
	public static void tick() {
		cooldown--;
		if(cooldown < 0)
			cooldown = 0;
	}
	
	public static void send(BlockPos pos) {
		if(cooldown > 0)
			return;
		cooldown = 10;
		CreateAddition.Network.sendToServer(new ObservePacket(pos));
	}
}

