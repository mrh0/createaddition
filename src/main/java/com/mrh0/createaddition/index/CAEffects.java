package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.mrh0.createaddition.effect.ShockingEffect;


public class CAEffects {
	
	public static MobEffect SHOCKING;
	public static void register() {
		SHOCKING = Registry.register(Registry.MOB_EFFECT, new ResourceLocation(CreateAddition.MODID, "shocking"), new ShockingEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, "6ed2d177-af97-423c-84f5-1f80c364639f", -100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	}
}
