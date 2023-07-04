package com.mrh0.createaddition.energy;

import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

public class NodeMovementBehaviour implements MovementBehaviour {
	
	@Override
	public void startMoving(MovementContext context) {
		// Mark this tileentity as a contraption.
		context.blockEntityData.putBoolean("contraption", true);
	}
}
