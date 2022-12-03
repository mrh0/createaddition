package com.mrh0.createaddition.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.energy.IEnergyStorage;

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
	
	public static ItemStack findStack(Item item, Inventory inv) {
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
	
	public static int getSkyLight(Level world, BlockPos pos) {
		return Math.max(world.getBrightness(LightLayer.SKY, pos) - world.getSkyDarken(), 0);
	}
	
	public static ItemStack mergeStack(ItemStack add, ItemStack to) {
		return new ItemStack(to.isEmpty()?add.getItem():to.getItem(), to.getCount() + add.getCount());
	}
	
	public static String format(int n) {
		if(n > 1000000)
			return Math.round((double)n/100000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
	
	public static Component getTextComponent(IEnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return Component.literal(nan);
		return Component.literal(format(ies.getEnergyStored())+unit).withStyle(ChatFormatting.AQUA).append(Component.literal(" / ").withStyle(ChatFormatting.GRAY)).append(Component.literal(format(ies.getMaxEnergyStored())+unit));
	}
	
	public static Component getTextComponent(IEnergyStorage ies) {
		return getTextComponent(ies, "NaN", "fe");
	}
}
