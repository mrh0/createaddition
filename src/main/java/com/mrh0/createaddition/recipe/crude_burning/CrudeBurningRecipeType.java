package com.mrh0.createaddition.recipe.crude_burning;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.item.crafting.IRecipeType;

public class CrudeBurningRecipeType implements IRecipeType<CrudeBurningRecipe> {
	
	@Override
	public String toString() {
		return CreateAddition.MODID+":crude_burning";
	}
}