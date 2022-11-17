package com.mrh0.createaddition.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class LiquidBurningCategory extends CARecipeCategory<LiquidBurningRecipe> {

	public LiquidBurningCategory(Info<LiquidBurningRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, LiquidBurningRecipe recipe, IFocusGroup focuses) {
		builder
			.addSlot(RecipeIngredientRole.INPUT, 15, 9)
			.setBackground(getRenderedSlot(), -1, -1)
			.addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredient().getMatchingFluidStacks()))
			.addTooltipCallback(addFluidTooltip(recipe.getFluidIngredient().getRequiredAmount()));
	}

	@Override
	public void draw(LiquidBurningRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		//AllGuiTextures.JEI_SLOT.render(stack, 14, 8);
		//AllGuiTextures.JEI_ARROW.render(stack, 85, 32);
		//AllGuiTextures.JEI_DOWN_ARROW.render(stack, 43, 4);

		Minecraft.getInstance().font.draw(stack, new TranslatableComponent("createaddition.recipe.liquid_burning.burn_time").getString(Integer.MAX_VALUE) + ": " + ((double)recipe.getBurnTime()/20d)+"s", 9, 34, 4210752);
	}
}