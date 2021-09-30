package com.mrh0.createaddition.effect;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class ShockingEffect extends Effect {
	public ShockingEffect() {
		super(EffectType.HARMFUL, 15453236);
		setRegistryName(new ResourceLocation(CreateAddition.MODID, "shocking"));
	}
}
