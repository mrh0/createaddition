package com.mrh0.createaddition.compat.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
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
