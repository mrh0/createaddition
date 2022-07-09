package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CARecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

	public CARecipeCategory(IDrawable icon, IDrawable background) {
		super(icon, background);
	}

	@Override
	public void setCategoryId(String name) {
		this.name = name;
		this.type = RecipeType.create(CreateAddition.MODID, name, this.getRecipeClass());
	}
	
	@Override
	public Component getTitle() {
		return new TranslatableComponent( CreateAddition.MODID + ".recipe." + name);
	}
}
