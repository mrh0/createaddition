package com.mrh0.createaddition.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fluids.FluidStack;

public class CrudeBurningCategory extends CARecipeCategory<CrudeBurningRecipe> {

	public CrudeBurningCategory() {
		super(itemIcon(CABlocks.CRUDE_BURNER.get()), emptyBackground(177, 53));
	}

	@Override
	public Class<? extends CrudeBurningRecipe> getRecipeClass() {
		return CrudeBurningRecipe.class;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CrudeBurningRecipe recipe, IFocusGroup focuses) {
		List<FluidStack> out = new ArrayList<FluidStack>();
		builder
				.addSlot(RecipeIngredientRole.INPUT, 82, 8)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(
						VanillaTypes.FLUID,
						withImprovedVisibility(recipe.getFluidIngredient().getMatchingFluidStacks())
				)
				.addTooltipCallback(addFluidTooltip());
	}

	@Override
	public void draw(CrudeBurningRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		Minecraft.getInstance().font.draw(matrixStack, new TranslatableComponent("createaddition.recipe.crude_burning.burn_time").getString(Integer.MAX_VALUE) + ": " + ((double)recipe.getBurnTime()/20d)+"s", 9, 34, 4210752);
	}
}