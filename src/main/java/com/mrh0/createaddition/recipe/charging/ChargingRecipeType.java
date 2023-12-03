package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.world.item.crafting.RecipeType;

public class ChargingRecipeType implements RecipeType<ChargingRecipe> {
	
	@Override
	public String toString() {
		return CreateAddition.MODID+":charging";
	}
}