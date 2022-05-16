package com.mrh0.createaddition.blocks.accumulator;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

public class AccumulatorMovementBehaviour implements MovementBehaviour {
	@Override
	public void startMoving(MovementContext c) {
		ConnectorMovementBehaviour.connectorStartMoving(c, AccumulatorTileEntity.NODE_COUNT);
	}
}
