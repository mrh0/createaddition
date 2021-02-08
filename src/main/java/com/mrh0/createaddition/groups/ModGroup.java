package com.mrh0.createaddition.groups;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModGroup extends ItemGroup{
	public static ModGroup MAIN;;
	
	public ModGroup(String name) {
		super(CreateAddition.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(CABlocks.ELECTRIC_MOTOR.get());
	}
}
