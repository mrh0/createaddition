package com.mrh0.createaddition.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class CARecipeSerializer <R extends IRecipe<?>> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<R> {
	public abstract ItemStack getIcon();

	@Override
	public final R read(ResourceLocation recipeId, JsonObject json) {
		if(CraftingHelper.processConditions(json, "conditions"))
			return readFromJson(recipeId, json);
		return readFromJson(recipeId, json);
	}

	protected ItemStack readOutput(JsonElement outputObject) {
		if(outputObject.isJsonObject() && outputObject.getAsJsonObject().has("item"))
			return ShapedRecipe.deserializeItem(outputObject.getAsJsonObject());
		return null;
	}

	public abstract R readFromJson(ResourceLocation recipeId, JsonObject json);
}
