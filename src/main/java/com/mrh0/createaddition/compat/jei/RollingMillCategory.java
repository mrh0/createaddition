package com.mrh0.createaddition.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;

public class RollingMillCategory extends CARecipeCategory<RollingRecipe> {

	private AnimatedRollingMill rolling_mill = new AnimatedRollingMill();

	public RollingMillCategory() {
		super(itemIcon(CABlocks.ROLLING_MILL.get()), emptyBackground(177, 53));
	}

	@Override
	public Class<? extends RollingRecipe> getRecipeClass() {
		return RollingRecipe.class;
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
	public void draw(RollingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
		rolling_mill.draw(matrixStack, 48, 27);
	}
}