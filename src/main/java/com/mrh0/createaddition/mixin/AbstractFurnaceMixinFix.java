package com.mrh0.createaddition.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Direction;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceMixinFix {
	public boolean canExtractItem(int index, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN && index == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET)
				return false;
		}
		if(dir == Direction.UP)
			return false;
		return true;
	}
}
