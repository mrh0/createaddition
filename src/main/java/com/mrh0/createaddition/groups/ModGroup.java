package com.mrh0.createaddition.groups;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;

import io.github.fabricators_of_create.porting_lib.util.LazyItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class ModGroup extends LazyItemGroup {
	public static ModGroup MAIN;
	
	public ModGroup(String name) {
		super(CreateAddition.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(CABlocks.ELECTRIC_MOTOR.get());
	}
}
