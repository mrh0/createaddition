package com.mrh0.createaddition.network;

import java.util.function.Supplier;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class RemoveConnectorPacket {
	private BlockPos pos;
	private int node;
	
	public RemoveConnectorPacket(BlockPos pos, int node) {
		this.pos = pos;
		this.node = node;
	}
	
	public static void encode(RemoveConnectorPacket packet, FriendlyByteBuf tag) {
        tag.writeBlockPos(packet.pos);
        tag.writeInt(packet.node);
    }
	
	public static RemoveConnectorPacket decode(FriendlyByteBuf buf) {
		RemoveConnectorPacket scp = new RemoveConnectorPacket(buf.readBlockPos(), buf.readInt());
        return scp;
    }
	
	public static void handle(RemoveConnectorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				
				handleData(pkt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private static void handleData(RemoveConnectorPacket pkt) {
		BlockEntity te = ClientMinecraftWrapper.getClientLevel().getBlockEntity(pkt.pos);
		System.out.println("RemoveConnectorPacket 1");
		if (te != null) {
			System.out.println("RemoveConnectorPacket 2");
			if(te instanceof IWireNode wn) {
				System.out.println("RemoveConnectorPacket 3");
				wn.removeNode(pkt.node);
			}
		}
	}

	/**
	 * @deprecated  Just use {@link SyncedTileEntity#notifyUpdate()} to send NBT data.
	 */
	@Deprecated(forRemoval = true)
	public static void send(BlockPos pos, int node, Level level) {
		CreateAddition.Network.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), new RemoveConnectorPacket(pos, node));
	}
}