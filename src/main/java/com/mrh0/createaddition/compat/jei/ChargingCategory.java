package com.mrh0.createaddition.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class ChargingCategory extends CARecipeCategory<ChargingRecipe> {

	private AnimatedTeslaCoil tesla_coil = new AnimatedTeslaCoil();

	public ChargingCategory() {
		super(itemIcon(CABlocks.TESLA_COIL.get()), emptyBackground(177, 53));
	}

	@Override
	public Class<? extends ChargingRecipe> getRecipeClass() {
		return ChargingRecipe.class;
	}

	@Override
	public void setIngredients(ChargingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(recipe.getResultItem());
		ingredients.setOutputs(VanillaTypes.ITEM, stacks);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, ChargingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		itemStacks.init(0, true, 14, 8);
		itemStacks.set(0, recipe.ingredient.getItems()[0]);

		ItemStack result = recipe.getResultItem();
		int yOffset = (0 / 2) * -19;

		itemStacks.init(1, false, 139, 27 + yOffset);
		itemStacks.set(1, result);

		//addStochasticTooltip(itemStacks, results);
	}

	@Override
	public void draw(ChargingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.render(matrixStack, 14, 8);
		AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
		tesla_coil.draw(matrixStack, 48, 27);
		
		Minecraft.getInstance().font.draw(matrixStack, Util.format(recipe.energy) + "fe", 86, 9, 4210752);

		getRenderedSlot(recipe, 0).render(matrixStack, 139, 27);
	}
}