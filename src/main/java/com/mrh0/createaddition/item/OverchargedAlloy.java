package com.mrh0.createaddition.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class OverchargedAlloy extends Item {

	public OverchargedAlloy(Properties props) {
		super(props);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
