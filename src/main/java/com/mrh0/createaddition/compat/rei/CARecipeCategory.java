package com.mrh0.createaddition.compat.rei;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.compat.rei.EmptyBackground;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public abstract class CARecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

	public CARecipeCategory(Renderer icon, EmptyBackground background) {
		super(icon, background);
	}

	@Override
	public void setCategoryId(String name) {
		this.uid = CategoryIdentifier.of(new ResourceLocation(CreateAddition.MODID, name));
		this.name = name;
	}
	
	@Override
	public Component getTitle() {
		return new TranslatableComponent( CreateAddition.MODID + ".recipe." + name);
	}
}
