package com.mrh0.createaddition.recipe.charging;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
/*
public class ChargingRecipeSerializer extends CARecipeSerializer<ChargingRecipe>{

	public ChargingRecipeSerializer() {
	}
	
	@Override
	public ChargingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int burnTime = buffer.readInt();
		FluidIngredient fluid = FluidIngredient.read(buffer);
		return new ChargingRecipe(recipeId, fluid, burnTime);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, ChargingRecipe recipe) {
		//buffer.writeInt(recipe.burnTime);
		//recipe.fluidIngredients.write(buffer);
	}
	
	@Override
	public ItemStack getIcon() {
		return CABlocks.CRUDE_BURNER.asStack();
	}

	@Override
	public ChargingRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
		int burnTime = (json.get("burnTime")).getAsInt();
		FluidIngredient fluid = FluidIngredient.deserialize(json.get("input"));
		return new ChargingRecipe(recipeId, fluid, burnTime);
	}
}*/