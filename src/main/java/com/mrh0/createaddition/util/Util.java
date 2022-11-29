package com.mrh0.createaddition.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class Util {
	
	public static ItemStack findStack(Item item, Inventory inv) {
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(stack.getItem() == item)
				return stack;
		}
		return ItemStack.EMPTY;
	}

	
	public static String format(long n) {
		if(n > 1000000)
			return Math.round((double)n/100000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
	
	public static Component getTextComponent(EnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return new TextComponent(nan);
		return new TextComponent(format(ies.getAmount())+unit).withStyle(ChatFormatting.AQUA).append(new TextComponent(" / ").withStyle(ChatFormatting.GRAY))
				.append(new TextComponent(format(ies.getCapacity())+unit));
	}
	
	public static Component getTextComponent(EnergyStorage ies) {
		return getTextComponent(ies, "NaN", "fe");
	}
}
