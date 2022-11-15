package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeSerializer;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeSerializer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class CARecipes {
	public static void register() {
		Registry
			.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(CreateAddition.MODID, "rolling"), new RollingRecipeSerializer());
		Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateAddition.MODID, "rolling"), RollingRecipe.TYPE);

		Registry
			.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(CreateAddition.MODID, "charging"), new ChargingRecipeSerializer());
		Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateAddition.MODID, "charging"), ChargingRecipe.TYPE);
	}
}
