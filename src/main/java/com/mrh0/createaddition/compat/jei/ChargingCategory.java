package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ChargingCategory extends CARecipeCategory<ChargingRecipe> {

	private AnimatedTeslaCoil tesla_coil = new AnimatedTeslaCoil();

	public ChargingCategory(Info<ChargingRecipe> info) {
		super(info);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ChargingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 15, 9)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.ingredient);

		builder
				.addSlot(RecipeIngredientRole.OUTPUT, 140, 28)
				.setBackground(getRenderedSlot(), -1, -1)
				.addItemStack(recipe.getResultItem(null));
	}

	@Override
	public void draw(ChargingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
		tesla_coil.draw(matrixStack, 48, 27);

		matrixStack.drawString(Minecraft.getInstance().font, Util.format(recipe.energy) + "fe", 86, 9, 4210752);
	}
}