package com.mrh0.createaddition.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class BiomassPelletItem extends Item {

	public BiomassPelletItem(Properties props) {
		super(props);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
		return 6400;
	}
}
