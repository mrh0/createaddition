package com.mrh0.createaddition.recipe.rolling;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.item.crafting.IRecipeType;

public class RollingRecipeType implements IRecipeType<RollingRecipe> {

	@Override
	public String toString() {
		return CreateAddition.MODID+":rolling";
	}
}
