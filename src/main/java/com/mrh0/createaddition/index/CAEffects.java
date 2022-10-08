package com.mrh0.createaddition.index;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.IForgeRegistry;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.effect.ShockingEffect;


public class CAEffects {
	public static MobEffect SHOCKING;
	public static void register(IForgeRegistry<MobEffect> reg) {
		SHOCKING = new ShockingEffect().addAttributeModifier(Attributes.MOVEMENT_SPEED, "6ed2d177-af97-423c-84f5-1f80c364639f", (double)-100f, AttributeModifier.Operation.MULTIPLY_TOTAL);
		reg.register(new ResourceLocation(CreateAddition.MODID, "shocking"), SHOCKING);
	}
}
