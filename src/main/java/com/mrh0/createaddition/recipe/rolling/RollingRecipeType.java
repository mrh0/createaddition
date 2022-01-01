package com.mrh0.createaddition.recipe.rolling;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.world.item.crafting.RecipeType;

public class RollingRecipeType implements RecipeType<RollingRecipe> {

	@Override
	public String toString() {
		return CreateAddition.MODID+":rolling";
	}
}
