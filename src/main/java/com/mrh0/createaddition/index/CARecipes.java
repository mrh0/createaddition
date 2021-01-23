package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeSerializer;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;

public class CARecipes {
	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		event.getRegistry()
			.register(new RollingRecipeSerializer().setRegistryName(new ResourceLocation(CreateAddition.MODID, "rolling")));
		Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateAddition.MODID, "rolling"), RollingRecipe.TYPE);
	}
}
