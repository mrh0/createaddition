package com.mrh0.createaddition.recipe.rolling;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RollingRecipeSerializer extends CARecipeSerializer<RollingRecipe>{

	public RollingRecipeSerializer() {
		
	}
	
	@Override
	public RollingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack output = buffer.readItemStack();
		Ingredient input = Ingredient.read(buffer);
		return new RollingRecipe(input, output, recipeId);
	}

	@Override
	public void write(PacketBuffer buffer, RollingRecipe recipe) {
		buffer.writeItemStack(recipe.output);
		recipe.ingredient.write(buffer);
	}

	@Override
	public ItemStack getIcon() {
		return CABlocks.ROLLING_MILL.asStack();
	}

	@Override
	public RollingRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack output = readOutput(json.get("result"));
		Ingredient input = Ingredient.deserialize(json.get("input"));
		return new RollingRecipe(input, output, recipeId);
	}

}
