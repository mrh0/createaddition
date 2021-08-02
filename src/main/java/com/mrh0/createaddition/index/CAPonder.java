package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.ponder.PonderScenes;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.content.PonderTag;
import net.minecraft.util.ResourceLocation;

public class CAPonder {
	public static final PonderTag ELECTRIC = new PonderTag(new ResourceLocation(CreateAddition.MODID, "electric")).item(CABlocks.ELECTRIC_MOTOR.get(), true, false)
			.defaultLang("Electric", "Components which use electricity");
	
	public static void register() {
		PonderRegistry.startRegistration(CreateAddition.MODID);

		PonderRegistry.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "electric_motor", PonderScenes::electricMotor, PonderTag.KINETIC_SOURCES, ELECTRIC);
		PonderRegistry.addStoryBoard(CABlocks.ALTERNATOR, "alternator", PonderScenes::alternator, PonderTag.KINETIC_APPLIANCES, ELECTRIC);
		PonderRegistry.addStoryBoard(CABlocks.ROLLING_MILL, "rolling_mill", PonderScenes::rollingMill, PonderTag.KINETIC_APPLIANCES);
		PonderRegistry.addStoryBoard(CABlocks.ROLLING_MILL, "automate_rolling_mill", PonderScenes::automateRollingMill, PonderTag.KINETIC_APPLIANCES);
		PonderRegistry.addStoryBoard(CABlocks.HEATER, "heater", PonderScenes::heater, PonderTag.LOGISTICS, ELECTRIC);
		if(CreateAddition.CC_ACTIVE)
			PonderRegistry.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "cc_electric_motor", PonderScenes::ccMotor, PonderTag.KINETIC_SOURCES, ELECTRIC);
		
		PonderRegistry.TAGS.forTag(PonderTag.KINETIC_SOURCES)
			.add(CABlocks.ELECTRIC_MOTOR);
		
		PonderRegistry.TAGS.forTag(PonderTag.KINETIC_APPLIANCES)
			.add(CABlocks.ROLLING_MILL)
			.add(CABlocks.ALTERNATOR);
		
		PonderRegistry.TAGS.forTag(PonderTag.LOGISTICS)
			.add(CABlocks.HEATER);
		
		PonderRegistry.TAGS.forTag(ELECTRIC)
			.add(CABlocks.ELECTRIC_MOTOR)
			.add(CABlocks.ALTERNATOR)
			.add(CABlocks.HEATER);

		PonderRegistry.endRegistration();
	}
}
