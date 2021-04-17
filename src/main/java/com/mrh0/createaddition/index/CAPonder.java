package com.mrh0.createaddition.index;

import com.mrh0.createaddition.ponder.PonderScenes;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.content.PonderTag;

public class CAPonder {
	public static void register() {
		PonderRegistry.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "electric_motor", PonderScenes::electricMotor, PonderTag.KINETIC_SOURCES);
		PonderRegistry.addStoryBoard(CABlocks.ALTERNATOR, "alternator", PonderScenes::alternator, PonderTag.KINETIC_APPLIANCES);
		PonderRegistry.addStoryBoard(CABlocks.ROLLING_MILL, "rolling_mill", PonderScenes::rollingMill, PonderTag.KINETIC_APPLIANCES);
		PonderRegistry.addStoryBoard(CABlocks.ROLLING_MILL, "automate_rolling_mill", PonderScenes::automateRollingMill, PonderTag.KINETIC_APPLIANCES);
		PonderRegistry.addStoryBoard(CABlocks.HEATER, "heater", PonderScenes::heater, PonderTag.LOGISTICS);
	}
}
