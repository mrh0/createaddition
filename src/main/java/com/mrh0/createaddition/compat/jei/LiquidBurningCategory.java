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

		Minecraft.getInstance().font.draw(stack, Component.translatable("createaddition.recipe.liquid_burning.burn_time").getString(Integer.MAX_VALUE) + ": " + ((double)recipe.getBurnTime()/20d)+"s", 9, 34, 4210752);
	}
	
	/*
	public LiquidBurningCategory() {
		super(itemIcon(AllBlocks.BLAZE_BURNER.get()), emptyBackground(177, 53));
	}

	@Override
	public Class<? extends CrudeBurningRecipe> getRecipeClass() {
		return CrudeBurningRecipe.class;
	}

	@Override
	public void setIngredients(CrudeBurningRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(new ArrayList<Ingredient>());
		ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<ItemStack>());
		ingredients.setInputLists(VanillaTypes.FLUID, NonNullList.of(recipe.getFluidIngredient().getMatchingFluidStacks()));
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, LiquidBurningRecipe recipe, IIngredients ingredients) {
		IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
		
		NonNullList<FluidIngredient> fluidIngredients = NonNullList.of(recipe.getFluidIngredient());
		
		List<FluidStack> out = new ArrayList<FluidStack>();
		
		fluidStacks.init(0, true, 81, 7);
		fluidStacks.set(0, withImprovedVisibility(recipe.getFluidIngredient().getMatchingFluidStacks()
				.stream()
				.map(fluid -> {
					out.add(fluid);
					return fluid;
				})
				.collect(Collectors.toList())));

		addFluidTooltip(fluidStacks, fluidIngredients, out);
	}

	@Override
	public void draw(LiquidBurningRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		//AllGuiTextures.JEI_SLOT.draw(matrixStack, 14, 8);
		//AllGuiTextures.JEI_ARROW.draw(matrixStack, 85, 32);
		//AllGuiTextures.JEI_DOWN_ARROW.draw(matrixStack, 43, 4);

		AllGuiTextures.JEI_SLOT.render(matrixStack, 80, 6);

		Minecraft.getInstance().font.draw(matrixStack, new TranslatableComponent("createaddition.recipe.crude_burning.burn_time").getString(Integer.MAX_VALUE) + ": " + ((double)recipe.getBurnTime()/20d)+"s", 9, 34, 4210752);
	}
	*/
}