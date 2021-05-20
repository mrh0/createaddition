package com.mrh0.createaddition.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OverchargedAlloy extends Item {

	public OverchargedAlloy(Properties props) {
		super(props);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
}
