package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
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

			/*if(IWireNode.readNodeWireType(be.getTileData(), index) == null)
				continue;
			if(IWireNode.readNodeWireType(c.tileData, i) == null)
				continue;*/
			IWireNode wn = (IWireNode)be;
			/*BlockPos myPos = IWireNode.readNodeBlockPos(be.getTileData(), 0);
			System.out.println("1REMOVED " + pos + ":" + myPos);

			if(ConnectorMovementManager.isUpdated(c.world, myPos)) {
				System.out.println("2REMOVED");
				continue;
			}
			ConnectorMovementManager.setUpdated(c.world, myPos);*/
			wn.preformRemoveOfNode(index);
			RemoveConnectorPacket.send(pos, index, c.world);
			IWireNode.clearNode(c.tileData, i);
			IWireNode.clearNode(((BlockEntityExtensions)be).getExtraCustomData(), index);
			//IWireNode.dropWire(c.world, pos, type.getDrop());
			//System.out.println("2REMOVED! " + pos.toString() + ":" + index + ":" + be.toString() + ":" + IWireNode.readNodeWireType(beExtension.getExtraCustomData(), index));
		}
	}
	
	@Override
	public void startMoving(MovementContext c) {
		MovementBehaviour.super.startMoving(c);
		//System.out.println("TEST: " + c.position);
		connectorStartMoving(c, ConnectorTileEntity.NODE_COUNT);
	}
}
