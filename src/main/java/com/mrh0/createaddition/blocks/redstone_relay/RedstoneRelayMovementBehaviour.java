package com.mrh0.createaddition.blocks.redstone_relay;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

public class RedstoneRelayMovementBehaviour implements MovementBehaviour {
	@Override
	public void startMoving(MovementContext c) {
		ConnectorMovementBehaviour.connectorStartMoving(c, RedstoneRelayTileEntity.NODE_COUNT);
	}
}
