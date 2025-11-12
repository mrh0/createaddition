package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.ponder.PonderScenes;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CAPonders {
	public static final ResourceLocation ELECTRIC = CreateAddition.asResource("electric");

	public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
		PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.registerTag(ELECTRIC)
				.addToIndex()
				.item(CABlocks.ELECTRIC_MOTOR.get(), true, false)
				.title("Electric Blocks")
				.description("Components which use electricity")
				.register();

		HELPER.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
				.add(CABlocks.ELECTRIC_MOTOR);

		HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
				.add(CABlocks.ROLLING_MILL)
				.add(CABlocks.ALTERNATOR);

		HELPER.addToTag(AllCreatePonderTags.FLUIDS)
				.add(CAItems.STRAW);

		HELPER.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR)
				.add(CABlocks.PORTABLE_ENERGY_INTERFACE);

		HELPER.addToTag(ELECTRIC)
				.add(CABlocks.ELECTRIC_MOTOR)
				.add(CABlocks.ALTERNATOR)
				.add(CABlocks.TESLA_COIL)
				.add(CABlocks.MODULAR_ACCUMULATOR)
				.add(CABlocks.PORTABLE_ENERGY_INTERFACE);
	}

	public static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "electric_motor", PonderScenes::electricMotor, AllCreatePonderTags.KINETIC_SOURCES, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.ALTERNATOR, "alternator", PonderScenes::alternator, AllCreatePonderTags.KINETIC_APPLIANCES, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.ROLLING_MILL, "rolling_mill", PonderScenes::rollingMill, AllCreatePonderTags.KINETIC_APPLIANCES);
		HELPER.addStoryBoard(CABlocks.ROLLING_MILL, "automate_rolling_mill", PonderScenes::automateRollingMill, AllCreatePonderTags.KINETIC_APPLIANCES);
		HELPER.addStoryBoard(CABlocks.TESLA_COIL, "tesla_coil", PonderScenes::teslaCoil, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.TESLA_COIL, "tesla_coil_hurt", PonderScenes::teslaCoilHurt, ELECTRIC);
		HELPER.addStoryBoard(CAItems.STRAW, "liquid_blaze_burner", PonderScenes::liquidBlazeBurner, AllCreatePonderTags.FLUIDS);
		HELPER.addStoryBoard(AllBlocks.BLAZE_BURNER, "liquid_blaze_burner", PonderScenes::liquidBlazeBurner, AllCreatePonderTags.LOGISTICS);
		HELPER.addStoryBoard(CABlocks.MODULAR_ACCUMULATOR, "accumulator", PonderScenes::modularAccumulator, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.PORTABLE_ENERGY_INTERFACE, "pei_transfer", PonderScenes::peiTransfer, AllCreatePonderTags.CONTRAPTION_ACTOR, ELECTRIC);
		HELPER.addStoryBoard(CABlocks.PORTABLE_ENERGY_INTERFACE, "pei_redstone", PonderScenes::peiRedstone, AllCreatePonderTags.CONTRAPTION_ACTOR, ELECTRIC);


		if(CreateAddition.CC_ACTIVE)
			HELPER.addStoryBoard(CABlocks.ELECTRIC_MOTOR, "cc_electric_motor", PonderScenes::ccMotor, AllCreatePonderTags.KINETIC_SOURCES, ELECTRIC);
	}
}