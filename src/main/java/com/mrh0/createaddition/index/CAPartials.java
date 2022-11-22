package com.mrh0.createaddition.index;

import com.jozufozu.flywheel.core.PartialModel;
import com.mrh0.createaddition.CreateAddition;

import net.minecraft.resources.ResourceLocation;

public class CAPartials {
	public static void initClass() {
	}

	public static final PartialModel LIQUID_HAT = entity("liquid_hat");
	
	@SuppressWarnings("unused")
	private static PartialModel block(String path) {
		return new PartialModel(new ResourceLocation(CreateAddition.MODID, "block/" + path));
	}

	private static PartialModel entity(@SuppressWarnings("SameParameterValue") String path) {
		return new PartialModel(new ResourceLocation(CreateAddition.MODID, "entity/" + path));
	}
}
