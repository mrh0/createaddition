package com.mrh0.createaddition.recipe.crude_burning;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.world.item.crafting.RecipeType;

public class CrudeBurningRecipeType implements RecipeType<CrudeBurningRecipe> {
	
	@Override
	public String toString() {
		return CreateAddition.MODID+":crude_burning";
	}
}