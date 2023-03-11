package com.mrh0.createaddition.energy;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

public class NodeMovementBehaviour implements MovementBehaviour {
	
	@Override
	public void startMoving(MovementContext context) {
		// Mark this tileentity as a contraption.
		context.tileData.putBoolean("contraption", true);
	}
}
