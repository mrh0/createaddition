package com.mrh0.createaddition.groups;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModGroup extends ItemGroup{
	public static ModGroup MAIN;;
	
	public ModGroup(String name) {
		super(CreateAddition.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Items.LEVER);
	}
}
