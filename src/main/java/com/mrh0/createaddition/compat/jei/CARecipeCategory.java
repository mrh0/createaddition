package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CARecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

	public CARecipeCategory(IDrawable icon, IDrawable background) {
		super(icon, background);
	}

	@Override
	public void setCategoryId(String name) {
		this.uid = new ResourceLocation(CreateAddition.MODID, name);
		this.name = name;
	}
	
	@Override
	public Component getTitle() {
		return new TranslatableComponent( CreateAddition.MODID + ".recipe." + name);
	}
}
