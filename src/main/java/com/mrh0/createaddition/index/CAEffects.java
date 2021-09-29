package com.mrh0.createaddition.index;

import net.minecraft.potion.Effect;
import net.minecraftforge.registries.IForgeRegistry;

import com.mrh0.createaddition.effect.ShockingEffect;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;

public class CAEffects {
	
	public static Effect SHOCKING;
	public static void register(IForgeRegistry<Effect> reg) {
		SHOCKING = new ShockingEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, "6ed2d177-af97-423c-84f5-1f80c364639f", (double)-100f, AttributeModifier.Operation.MULTIPLY_TOTAL);
		reg.register(SHOCKING);
	}
}
