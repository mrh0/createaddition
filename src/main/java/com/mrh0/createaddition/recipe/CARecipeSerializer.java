package com.mrh0.createaddition.recipe;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraft.world.item.crafting.ShapedRecipe;

public abstract class CARecipeSerializer <R extends Recipe<?>> implements RecipeSerializer<R> {
	public abstract ItemStack getIcon();

	@Override
	public final R fromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		if(CraftingHelper.processConditions(json, "conditions", context))
			return readFromJson(recipeId, json, context);
		return null;
	}

	@Override
	public R fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject serializedRecipe)
	{
		return null;
	}

	protected ItemStack readOutput(JsonElement outputObject) {
		if(outputObject.isJsonObject() && outputObject.getAsJsonObject().has("item"))
			return ShapedRecipe.itemStackFromJson(outputObject.getAsJsonObject());
		return null;
	}

	public abstract R readFromJson(ResourceLocation recipeId, JsonObject json, IContext context);
}
