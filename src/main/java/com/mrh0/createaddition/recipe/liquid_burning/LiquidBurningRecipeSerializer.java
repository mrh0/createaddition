package com.mrh0.createaddition.recipe.liquid_burning;

import com.google.gson.JsonObject;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.CARecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;

public class LiquidBurningRecipeSerializer extends CARecipeSerializer<LiquidBurningRecipe>{

	public LiquidBurningRecipeSerializer() {
		// TODO: Should move
		//FluidTags.bind("forge:plantoil");
	}

	@Override
	public LiquidBurningRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		boolean superheated = buffer.readBoolean();
		int burnTime = buffer.readInt();
		FluidIngredient fluid = FluidIngredient.read(buffer);
		return new LiquidBurningRecipe(recipeId, fluid, burnTime, superheated);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LiquidBurningRecipe recipe) {
		buffer.writeBoolean(recipe.superheated);
		buffer.writeInt(recipe.burnTime);
		recipe.fluidIngredients.write(buffer);
	}

	@Override
	public ItemStack getIcon() {
		return CABlocks.LIQUID_BLAZE_BURNER.asStack();
	}

	@Override
	public LiquidBurningRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		int burnTime = GsonHelper.getAsInt(json, "burnTime");
		FluidIngredient fluid = FluidIngredient.deserialize(json.get("input"));
		boolean superheated = GsonHelper.getAsBoolean(json, "superheated", false);

		//HeatCondition.deserialize(GsonHelper.getAsString(json, "heatProduced"));

		return new LiquidBurningRecipe(recipeId, fluid, burnTime, superheated);
	}
}
