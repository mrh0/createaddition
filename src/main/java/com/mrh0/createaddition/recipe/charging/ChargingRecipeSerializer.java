package com.mrh0.createaddition.recipe.charging;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;

public class ChargingRecipeSerializer extends CARecipeSerializer<ChargingRecipe> {
	
	@Override
	public ChargingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int maxChargeRate = buffer.readInt();
		int energy = buffer.readInt();
		ItemStack output = buffer.readItem();
		Ingredient input = Ingredient.fromNetwork(buffer);
		return new ChargingRecipe(recipeId, input, output, energy, maxChargeRate);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, ChargingRecipe recipe) {
		buffer.writeInt(recipe.maxChargeRate);
		buffer.writeInt(recipe.energy);
		buffer.writeItem(recipe.output);
		recipe.ingredient.toNetwork(buffer);
	}
	
	@Override
	public ItemStack getIcon() {
		return CABlocks.TESLA_COIL.asStack();
	}

	@Override
	public ChargingRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		int maxChargeRate = Integer.MAX_VALUE;
		if(json.has("maxChargeRate")) maxChargeRate = json.get("maxChargeRate").getAsInt();
		int energy = json.get("energy").getAsInt();
		ItemStack output = readOutput(json.get("result"));
		Ingredient input = Ingredient.fromJson(json.get("input"));
		return new ChargingRecipe(recipeId, input, output, energy, maxChargeRate);
	}
}

