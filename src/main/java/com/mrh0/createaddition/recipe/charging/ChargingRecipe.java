package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ChargingRecipe extends ProcessingRecipe<RecipeWrapper> {

	static int counter = 0;

	public static ChargingRecipe create(ItemStack from, ItemStack to) {
		ResourceLocation recipeId = new ResourceLocation(CreateAddition.MODID, "charging_" + counter++);
		return new ProcessingRecipeBuilder<>(ChargingRecipe::new, recipeId)
			.withItemIngredients(Ingredient.of(from))
			.withSingleItemOutput(to)
			.build();
	}

	public ChargingRecipe(ProcessingRecipeParams params) {
		super(AllRecipeTypes.CONVERSION, params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, World worldIn) {
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