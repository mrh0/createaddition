package com.mrh0.createaddition.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class Util {
	public static int min(int...v) {
		int m = Integer.MAX_VALUE;
		for(int i : v)
			if(i < m)
				m = i;
		return m;
	}
	
	public static int minIndex(int...v) {
		int m = 0;
		for(int i = 0; i < v.length; i++)
			if(v[i] < v[m])
				m = i;
		return m;
	}
	
	public static ItemStack findStack(Item item, PlayerInventory inv) {
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(stack.getItem() == item)
				return stack;
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean canStack(ItemStack add, ItemStack to){
		return add.getCount() + to.getCount() <= to.getMaxStackSize() && (add.getItem() == to.getItem()) || to.isEmpty();
	}
	
	public static int getMergeRest(ItemStack add, ItemStack to){
		return Math.max(add.getCount() + to.getCount() - to.getMaxStackSize(), 0);
	}
	
	public static int getSkyLight(World world, BlockPos pos) {
		return Math.max(world.getBrightness(LightType.SKY, pos) - world.getSkyDarken(), 0);
	}
	
	public static ItemStack mergeStack(ItemStack add, ItemStack to) {
		return new ItemStack(to.isEmpty()?add.getItem():to.getItem(), to.getCount() + add.getCount());
	}
}
