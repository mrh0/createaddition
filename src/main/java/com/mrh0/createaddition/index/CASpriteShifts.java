package com.mrh0.createaddition.index;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;

import net.minecraft.util.ResourceLocation;

import static com.simibubi.create.foundation.block.connected.CTSpriteShifter.getCT;
import static com.simibubi.create.foundation.block.connected.CTSpriteShifter.CTType.OMNIDIRECTIONAL;

import com.mrh0.createaddition.CreateAddition;

public class CASpriteShifts {
	public static final CTSpriteShiftEntry OVERCHARGED_CASING = getCT(OMNIDIRECTIONAL,  new ResourceLocation(CreateAddition.MODID, "block/overcharged_casing/overcharged_casing"), new ResourceLocation(CreateAddition.MODID, "block/overcharged_casing/overcharged_casing_connected"));
}
