package com.mrh0.createaddition.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

@Mixin(AbstractFurnaceBlockEntity.class)
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
