package com.mrh0.createaddition.recipe.crude_burning;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;

public class CrudeBurningRecipeSerializer extends CARecipeSerializer<CrudeBurningRecipe>{

	public CrudeBurningRecipeSerializer() {
		// TODO: Should move
		FluidTags.makeWrapperTag("forge:plantoil");
	}
	
	@Override
	public CrudeBurningRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int burnTime = buffer.readInt();
		FluidIngredient fluid = FluidIngredient.read(buffer);
		return new CrudeBurningRecipe(recipeId, fluid, burnTime);
	}

	@Override
	public void write(PacketBuffer buffer, CrudeBurningRecipe recipe) {
		buffer.writeInt(recipe.burnTime);
		recipe.fluidIngredients.write(buffer);
	}

	@Override
	public ItemStack getIcon() {
		return CABlocks.CRUDE_BURNER.asStack();
	}

	@Override
	public CrudeBurningRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
		int burnTime = (json.get("burnTime")).getAsInt();
		FluidIngredient fluid = FluidIngredient.deserialize(json.get("input"));
		return new CrudeBurningRecipe(recipeId, fluid, burnTime);
	}

}

