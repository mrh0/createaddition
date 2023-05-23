package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.ponder.PonderScenes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.resources.ResourceLocation;

public class CAPonder {
	public static final PonderTag ELECTRIC = new PonderTag(new ResourceLocation(CreateAddition.MODID, "electric")).item(CABlocks.ELECTRIC_MOTOR.get(), true, false)
			.defaultLang("Electric", "Components which use electricity");
	
	static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateAddition.MODID);
	
	public static void register() {
		HELPER.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "electric_motor", PonderScenes::electricMotor, AllPonderTags.KINETIC_SOURCES, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.ALTERNATOR, "alternator", PonderScenes::alternator, AllPonderTags.KINETIC_APPLIANCES, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.ROLLING_MILL, "rolling_mill", PonderScenes::rollingMill, AllPonderTags.KINETIC_APPLIANCES);
		HELPER.addStoryBoard(CABlocks.ROLLING_MILL, "automate_rolling_mill", PonderScenes::automateRollingMill, AllPonderTags.KINETIC_APPLIANCES);
		HELPER.addStoryBoard(CABlocks.TESLA_COIL, "tesla_coil", PonderScenes::teslaCoil, AllPonderTags.LOGISTICS, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.TESLA_COIL, "tesla_coil_hurt", PonderScenes::teslaCoilHurt, AllPonderTags.LOGISTICS, ELECTRIC);
		HELPER.addStoryBoard(CAItems.STRAW, "liquid_blaze_burner", PonderScenes::liquidBlazeBurner, AllPonderTags.LOGISTICS);
		HELPER.addStoryBoard(AllBlocks.BLAZE_BURNER, "liquid_blaze_burner", PonderScenes::liquidBlazeBurner, AllPonderTags.LOGISTICS);
		HELPER.addStoryBoard(CABlocks.MODULAR_ACCUMULATOR, "accumulator", PonderScenes::modularAccumulator, AllPonderTags.LOGISTICS, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.PORTABLE_ENERGY_INTERFACE, "pei_transfer", PonderScenes::peiTransfer, AllPonderTags.LOGISTICS, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.PORTABLE_ENERGY_INTERFACE, "pei_redstone", PonderScenes::peiRedstone, AllPonderTags.LOGISTICS, ELECTRIC);
		
		
		if(CreateAddition.CC_ACTIVE)
			HELPER.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "cc_electric_motor", PonderScenes::ccMotor, AllPonderTags.KINETIC_SOURCES, ELECTRIC);
		
		PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_SOURCES)
			.add(CABlocks.ELECTRIC_MOTOR);
		

		PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
			.add(CABlocks.ROLLING_MILL)
			.add(CABlocks.ALTERNATOR);
		
		PonderRegistry.TAGS.forTag(AllPonderTags.LOGISTICS)
			.add(CABlocks.TESLA_COIL)
			.add(CABlocks.MODULAR_ACCUMULATOR)
			.add(CABlocks.PORTABLE_ENERGY_INTERFACE);
		
		PonderRegistry.TAGS.forTag(ELECTRIC)
			.add(CABlocks.ELECTRIC_MOTOR)
			.add(CABlocks.ALTERNATOR)
			.add(CABlocks.TESLA_COIL)
			.add(CABlocks.MODULAR_ACCUMULATOR)
			.add(CABlocks.PORTABLE_ENERGY_INTERFACE);
	}
}