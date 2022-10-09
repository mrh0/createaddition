package com.mrh0.createaddition.effect;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;


public class ShockingEffect extends MobEffect {
	public ShockingEffect() {
		super(MobEffectCategory.HARMFUL, 15453236);
		//setRegistryName(new ResourceLocation(CreateAddition.MODID, "shocking"));
	}
}
