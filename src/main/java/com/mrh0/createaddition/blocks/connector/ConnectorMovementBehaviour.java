package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.debug.AdditionDebugger;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.network.RemoveConnectorPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConnectorMovementBehaviour implements MovementBehaviour {
	public static void connectorStartMoving(MovementContext c, int nodeCount) {
		/*for(int i = 0; i < nodeCount; i++) {
			if (true) continue; // Debug
			BlockPos pos = IWireNode.readNodeBlockPos(c.tileData, i, null); // Don't use null..
			WireType type = IWireNode.readNodeWireType(c.tileData, i);
			int index = IWireNode.readNodeIndex(c.tileData, i);
			
			BlockEntity be = c.world.getBlockEntity(pos);
			
			if(be == null)
				continue;
			if(!(be instanceof IWireNode))
				continue;*/
			
			/*if(IWireNode.readNodeWireType(be.getTileData(), index) == null)
				continue;
			if(IWireNode.readNodeWireType(c.tileData, i) == null)
				continue;*/
			//IWireNode wn = (IWireNode)be;
			/*BlockPos myPos = IWireNode.readNodeBlockPos(be.getTileData(), 0);
			System.out.println("1REMOVED " + pos + ":" + myPos);
			
			if(ConnectorMovementManager.isUpdated(c.world, myPos)) {
				System.out.println("2REMOVED");
				continue;
			}
			ConnectorMovementManager.setUpdated(c.world, myPos);*/
			/*wn.preformRemoveOfNode(index);
			RemoveConnectorPacket.send(pos, index, c.world);
			IWireNode.clearNode(c.tileData, i);
			IWireNode.clearNode(be.getTileData(), index);
			//IWireNode.dropWire(c.world, pos, type.getDrop());
			//System.out.println("2REMOVED! " + pos.toString() + ":" + index + ":" + be.toString() + ":" + IWireNode.readNodeWireType(be.getTileData(), index));
		}*/
	}
	
	@Override
	public void startMoving(MovementContext context) {
		// Mark this tileentity as a contraption.
		context.tileData.putBoolean("contraption", true);
		System.out.println("STARTED MOVING - Data: " + context.tileData);

		//System.out.println("TEST: " + c.position);
		//if (!c.world.isClientSide) System.out.println("Started moving, pos: " + c.position + ", localPos: " + c.localPos + ", state: " + c.state + ", con: " + c.contraption);
		//connectorStartMoving(c, ConnectorTileEntity.NODE_COUNT);
	}

	@Override
	public void stopMoving(MovementContext context) {
		/*if (!context.world.isClientSide) {
			System.out.println("Stopped moving, pos: " + context.position + ", localPos: " + context.localPos + ", state: " + context.state + ", con: " + context.contraption);
		}*/
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		//if (!context.world.isClientSide) System.out.println("Visited new position, pos: " + context.position + ", localPos: " + context.localPos + ", state: " + context.state);
	}

	@Override
	public void cancelStall(MovementContext context) {
		//if (!context.world.isClientSide) System.out.println("Cancelled stall, pos: " + context.position + ", localPos: " + context.localPos + ", state: " + context.state);
	}
}
