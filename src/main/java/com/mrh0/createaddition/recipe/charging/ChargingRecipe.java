package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;

import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class ChargingRecipe extends ProcessingRecipe<RecipeWrapper> {

	public static ChargingRecipe create(ItemStack from, ItemStack to) {
		ResourceLocation recipeId = new ResourceLocation(CreateAddition.MODID, "charging");
		return new ProcessingRecipeBuilder<>(ChargingRecipe::new, recipeId)
			.withItemIngredients(Ingredient.of(from))
			.withSingleItemOutput(to)
			.build();
	}

	public ChargingRecipe(ProcessingRecipeParams params) {
		super(AllRecipeTypes.CONVERSION, params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level worldIn) {
		return false;
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}

}