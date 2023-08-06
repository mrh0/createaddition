package com.mrh0.createaddition.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class RollingMillCategory extends CARecipeCategory<RollingRecipe> {

	private AnimatedRollingMill rolling_mill = new AnimatedRollingMill();

	public RollingMillCategory(Info<RollingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RollingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredient());

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 140, 28)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(recipe.getResultItem());
	}

	@Override
	public void draw(RollingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gg, double mouseX,
					 double mouseY) {
		AllGuiTextures.JEI_ARROW.render(gg, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(gg, 43, 4);
		rolling_mill.draw(gg, 48, 27);
	}
}