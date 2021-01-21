package com.mrh0.createaddition.item;

import com.simibubi.create.content.curiosities.tools.SandPaperItem;

import net.minecraft.item.ItemStack;

public class DiamondGritSandpaper extends SandPaperItem {

	public DiamondGritSandpaper(Properties properties) {
		super(properties);
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		return 1024;
	}
}
