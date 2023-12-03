package com.mrh0.createaddition.recipe.liquid_burning;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.world.item.crafting.RecipeType;

public class LiquidBurningRecipeType implements RecipeType<LiquidBurningRecipe> {
	@Override
	public String toString() {
		return CreateAddition.MODID+":crude_burning";
	}
}