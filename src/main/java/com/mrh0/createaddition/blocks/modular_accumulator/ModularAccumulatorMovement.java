package com.mrh0.createaddition.blocks.modular_accumulator;

import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

public class ModularAccumulatorMovement implements MovementBehaviour {

	@Override
	public void tick(MovementContext context) {
		// Try to add the modular accumulator every tick, it isn't optimal, but it's a workaround
		// for not having a way to detect when a contraption is loaded on world start.
		PortableEnergyManager.add(context);
	}

	@Override
	public void startMoving(MovementContext context) {
		CADebugger.print(context.world, "Contraption: " + context.contraption);
		CADebugger.print(context.world, "Contraption.Entity: " + context.contraption.entity);
		CADebugger.print(context.world, "FirstMovement: " + context.firstMovement);
		CADebugger.print(context.world, "Data: " + context.data.toString());
		CADebugger.print(context.world, "TileData: " + context.tileData.toString());
		PortableEnergyManager.add(context);
	}
}
