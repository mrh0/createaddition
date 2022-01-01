package com.mrh0.createaddition.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.world.item.ItemStack;

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
	public void setIngredients(RollingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ArrayList<ItemStack> stacks = new ArrayList<>();
		stacks.add(recipe.getResultItem());
		ingredients.setOutputs(VanillaTypes.ITEM, stacks);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RollingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		itemStacks.init(0, true, 14, 8);
		itemStacks.set(0, Arrays.asList(recipe.getIngredient().getItems()));

		ItemStack result = recipe.getResultItem();
		int yOffset = (0 / 2) * -19;

		itemStacks.init(1, false, 139, 27 + yOffset);
		itemStacks.set(1, result);

		//addStochasticTooltip(itemStacks, results);
	}

	@Override
	public void draw(RollingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SLOT.render(matrixStack, 14, 8);
		AllGuiTextures.JEI_ARROW.render(matrixStack, 85, 32);
		AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 43, 4);
		rolling_mill.draw(matrixStack, 48, 27);

		getRenderedSlot(recipe, 0).render(matrixStack, 139, 27);
	}
}