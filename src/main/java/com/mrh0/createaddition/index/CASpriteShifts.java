package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.minecraft.resources.ResourceLocation;

import static com.simibubi.create.foundation.block.connected.AllCTTypes.OMNIDIRECTIONAL;
import static com.simibubi.create.foundation.block.connected.CTSpriteShifter.getCT;

public class CASpriteShifts {
	public static final CTSpriteShiftEntry OVERCHARGED_CASING = getCT(OMNIDIRECTIONAL,  new ResourceLocation(CreateAddition.MODID, "block/overcharged_casing/overcharged_casing"), new ResourceLocation(CreateAddition.MODID, "block/overcharged_casing/overcharged_casing_connected"));
}
