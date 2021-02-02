package com.mrh0.createaddition.energy;

import net.minecraft.item.ItemStack;

public enum WireType {
	COPPER(0, 256, 0, null),
	GOLD(1, 1024, 0, null),
	ELECTRUM(2, 8196, 0, null);
	
	public final int ID, TRANSFER, COLOR;
	
	private WireType(int id, int transfer, int color, ItemStack drop) {
		ID = id;
		TRANSFER = transfer;
		COLOR = color;
	}
}
