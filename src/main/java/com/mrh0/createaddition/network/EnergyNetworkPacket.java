package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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
	
	public static void encode(EnergyNetworkPacket packet, PacketBuffer tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.demand);
        tag.writeInt(packet.buff);
    }
	
	public static EnergyNetworkPacket decode(PacketBuffer buf) {
		EnergyNetworkPacket scp = new EnergyNetworkPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
        return scp;
    }
	
	public static void handle(EnergyNetworkPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				updateClientCache(pkt.pos, pkt.demand, pkt.buff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private static void updateClientCache(BlockPos pos, int demand, int buff) {
		/*TileEntity te = (TileEntity) Minecraft.getInstance().world.getTileEntity(pos);
        if (te == null)
        	return;
        if(!(te instanceof IWireNode))
        	return;
        IWireNode ote = (IWireNode) te;*/
		//clientSaturation = saturation;
		clientDemand = demand;
		clientBuff = buff;
		clientSaturation = buff - demand;
    }
	
	public static void send(BlockPos pos, int demand, int buff, ServerPlayerEntity player) {
		CreateAddition.Network.send(PacketDistributor.PLAYER.with(() -> player), new EnergyNetworkPacket(pos, demand, buff));
	}
}
