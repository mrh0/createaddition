package com.mrh0.createaddition.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.item.Item.Properties;

public class OverchargedAlloy extends Item {

	public OverchargedAlloy(Properties props) {
		super(props);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
