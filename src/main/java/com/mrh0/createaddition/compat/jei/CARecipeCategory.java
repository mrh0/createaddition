package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class CARecipeCategory<T extends IRecipe<?>> extends CreateRecipeCategory<T> {

	public CARecipeCategory(IDrawable icon, IDrawable background) {
		super(icon, background);
	}

	@Override
	public void setCategoryId(String name) {
		this.uid = new ResourceLocation(CreateAddition.MODID, name);
		this.name = name;
	}
	
	@Override
	public String getTitle() {
		return new TranslationTextComponent( CreateAddition.MODID + ".recipe." + name).getString(Integer.MAX_VALUE);
	}
}
