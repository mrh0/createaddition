package com.mrh0.createaddition.item;

import com.mrh0.createaddition.index.CAItems;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class BiomassPelletBlock extends BlockItem {

	public BiomassPelletBlock(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
		FuelRegistry.INSTANCE.add(this, getBurnTime());
	}

	public static int getBurnTime() {
		return BiomassPellet.getBurnTime() * 9;
	}
}
