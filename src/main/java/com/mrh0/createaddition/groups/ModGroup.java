package com.mrh0.createaddition.groups;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class ModGroup {
	public static CreativeModeTab MAIN = FabricItemGroupBuilder.build(new ResourceLocation(CreateAddition.MODID, "main"), () -> new ItemStack(CABlocks.ELECTRIC_MOTOR.get()));
}
