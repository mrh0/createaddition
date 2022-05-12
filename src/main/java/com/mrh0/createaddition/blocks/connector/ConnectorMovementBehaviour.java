package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConnectorMovementBehaviour implements MovementBehaviour {
	public static void connectorStartMoving(MovementContext c, int nodeCount) {
		for(int i = 0; i < nodeCount; i++) {
			BlockPos pos = IWireNode.readNodeBlockPos(c.tileData, i);
			WireType type = IWireNode.readNodeWireType(c.tileData, i);
			int index = IWireNode.readNodeIndex(c.tileData, i);
			
			BlockEntity be = c.world.getBlockEntity(pos);
			if(be == null)
				continue;
			if(!(be instanceof IWireNode))
				continue;
			IWireNode wn = (IWireNode)be;
			wn.preformRemoveOfNode(index);
			RemoveConnectorPacket.send(pos, index, c.world);
			IWireNode.clearNode(c.tileData, i);
			IWireNode.dropWire(c.world, pos, type.getDrop());
			System.out.println("REMOVED! " + pos.toString() + ":" + index + ":" + be.toString());
		}
	}
	
	@Override
	public void startMoving(MovementContext c) {
		connectorStartMoving(c, ConnectorTileEntity.NODE_COUNT);
	}
}
