package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CARecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

	public CARecipeCategory(Info<T> info) {
		super(info);
	}
/*
	@Override
	public Component getTitle() {
		return new TranslatableComponent( CreateAddition.MODID + ".recipe." + name);
	}
 */
}
