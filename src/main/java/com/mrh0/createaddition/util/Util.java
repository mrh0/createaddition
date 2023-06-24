package com.mrh0.createaddition.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class Util {
	public static int max(int...v) {
		int m = Integer.MIN_VALUE;
		for(int i : v)
			if(i > m)
				m = i;
		return m;
	}

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

	
	public static String format(long n) {
		if(n > 1000_000_000)
			return Math.round((double)n/100_000_000d)/10d + "G";
		if(n > 1000_000)
			return Math.round((double)n/100_000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
	
	public static MutableComponent getTextComponent(EnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return new TextComponent(nan);
		return getTextComponent(ies.getAmount(), unit).withStyle(ChatFormatting.AQUA).append(new TextComponent(" / ").withStyle(ChatFormatting.GRAY))
				.append(getTextComponent(ies.getCapacity(), unit));
	}
	
	public static MutableComponent getTextComponent(EnergyStorage ies) {
		return getTextComponent(ies, "NaN", "fe");
	}

	public static MutableComponent getTextComponent(int value, String unit) {
		return new TextComponent(format(value)+unit);
	}
}