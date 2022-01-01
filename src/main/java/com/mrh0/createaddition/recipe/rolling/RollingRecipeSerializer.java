package com.mrh0.createaddition.recipe.rolling;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RollingRecipeSerializer extends CARecipeSerializer<RollingRecipe>{

	public RollingRecipeSerializer() {}
	
	@Override
	public RollingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack output = buffer.readItem();
		Ingredient input = Ingredient.fromNetwork(buffer);
		return new RollingRecipe(input, output, recipeId);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, RollingRecipe recipe) {
		buffer.writeItem(recipe.output);
		recipe.ingredient.toNetwork(buffer);
	}

	@Override
	public ItemStack getIcon() {
		return CABlocks.ROLLING_MILL.asStack();
	}

	@Override
	public RollingRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack output = readOutput(json.get("result"));
		Ingredient input = Ingredient.fromJson(json.get("input"));
		return new RollingRecipe(input, output, recipeId);
	}

}
