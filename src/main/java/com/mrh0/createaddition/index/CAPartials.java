package com.mrh0.createaddition.index;

import com.jozufozu.flywheel.core.PartialModel;
import com.mrh0.createaddition.CreateAddition;

import net.minecraft.resources.ResourceLocation;

public class CAPartials {

	public static final PartialModel LIQUID_HAT = entity("liquid_hat");
	public static final PartialModel SMALL_LIGHT = block("connector/small_light");
	public static final PartialModel ACCUMULATOR_GUAGE = block("modular_accumulator/guage");
	public static final PartialModel ACCUMULATOR_DIAL = block("modular_accumulator/dial");
	public static final PartialModel PORTABLE_ENERGY_INTERFACE_MIDDLE = block("portable_energy_interface/block_middle");
	public static final PartialModel PORTABLE_ENERGY_INTERFACE_MIDDLE_POWERED = block("portable_energy_interface/block_middle_powered");
	public static final PartialModel PORTABLE_ENERGY_INTERFACE_TOP = block("portable_energy_interface/block_top");

	private static PartialModel block(String path) {
		return new PartialModel(new ResourceLocation(CreateAddition.MODID, "block/" + path));
	}

	private static PartialModel entity(String path) {
		return new PartialModel(new ResourceLocation(CreateAddition.MODID, "entity/" + path));
	}

	public static void init() {
		// init static fields
	}
}
