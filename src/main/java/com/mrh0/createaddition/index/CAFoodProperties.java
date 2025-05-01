package com.mrh0.createaddition.index;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class CAFoodProperties {
    public static final FoodProperties CAKE_SLICE = (new FoodProperties.Builder())
			.nutrition(2).saturationModifier(0.1f).fast()
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, false, false), 1.0F).build();
}
