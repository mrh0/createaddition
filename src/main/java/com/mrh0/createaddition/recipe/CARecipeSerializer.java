package com.mrh0.createaddition.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraft.world.item.crafting.ShapedRecipe;

public abstract class CARecipeSerializer <R extends Recipe<?>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R> {
	public abstract ItemStack getIcon();

	@Override
	public final R fromJson(ResourceLocation recipeId, JsonObject json) {
		if(CraftingHelper.processConditions(json, "conditions"))
			return readFromJson(recipeId, json);
		return readFromJson(recipeId, json);
	}

	protected ItemStack readOutput(JsonElement outputObject) {
		if(outputObject.isJsonObject() && outputObject.getAsJsonObject().has("item"))
			return ShapedRecipe.itemStackFromJson(outputObject.getAsJsonObject());
		return null;
	}

	public abstract R readFromJson(ResourceLocation recipeId, JsonObject json);
}