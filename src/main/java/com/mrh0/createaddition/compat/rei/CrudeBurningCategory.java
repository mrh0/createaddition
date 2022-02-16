package com.mrh0.createaddition.compat.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.lib.transfer.fluid.FluidStack;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrudeBurningCategory extends CARecipeCategory<CrudeBurningRecipe> {

	public CrudeBurningCategory() {
		super(itemIcon(CABlocks.CRUDE_BURNER), emptyBackground(177, 53));
	}

	@Override
	public void addWidgets(CreateDisplay<CrudeBurningRecipe> display, List<Widget> ingredients, Point origin) {
		NonNullList<FluidIngredient> fluidIngredients = NonNullList.of(display.getRecipe().getFluidIngredient());

		List<FluidStack> out = new ArrayList<>();

		ingredients.add(basicSlot(origin.x + 81, origin.y + 7)
				.markInput()
				.entries(EntryIngredients.of(convertToREIFluid(display.getRecipe().getFluidIngredient().getMatchingFluidStacks()
						.stream()
						.map(fluid -> {
							out.add(fluid);
							return fluid;
						})
						.collect(Collectors.toList()).get(0)))));

		addFluidTooltip(ingredients, fluidIngredients, out);
	}

	@Override
	public void draw(CrudeBurningRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		//AllGuiTextures.JEI_SLOT.draw(matrixStack, 14, 8);
		//AllGuiTextures.JEI_ARROW.draw(matrixStack, 85, 32);
		//AllGuiTextures.JEI_DOWN_ARROW.draw(matrixStack, 43, 4);

		AllGuiTextures.JEI_SLOT.render(matrixStack, 80, 6);

		Minecraft.getInstance().font.draw(matrixStack, new TranslatableComponent("createaddition.recipe.crude_burning.burn_time").getString(Integer.MAX_VALUE) + ": " + ((double)recipe.getBurnTime()/20d)+"s", 9, 34, 4210752);
	}
}