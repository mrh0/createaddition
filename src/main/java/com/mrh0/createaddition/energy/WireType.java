package com.mrh0.createaddition.energy;

import net.minecraft.item.ItemStack;
import com.mrh0.createaddition.index.*;;

public enum WireType {
	COPPER(0, 256, 78, 37, 30, new ItemStack(CAItems.COPPER_WIRE.get(), 4), new ItemStack(CAItems.COPPER_SPOOL.get())),
	GOLD(1, 1024, 98, 83, 29, new ItemStack(CAItems.GOLD_WIRE.get(), 4), new ItemStack(CAItems.GOLD_SPOOL.get())),
	ELECTRUM(2, 8196, 0, 0, 0, ItemStack.EMPTY, ItemStack.EMPTY);
	
	private final int ID, TRANSFER, CR, CG, CB;
	private final ItemStack DROP;
	private final ItemStack SOURCE_DROP;
	
	private WireType(int id, int transfer, int red, int green, int blue, ItemStack drop, ItemStack source) {
		ID = id;
		TRANSFER = transfer;
		CR = red;
		CG = green;
		CB = blue;
		DROP = drop;
		SOURCE_DROP = source;
	}
	
	public static WireType fromIndex(int index) {
		switch(index) {
			case 0:
				return COPPER;
			case 1:
				return GOLD;
			case 2:
				return ELECTRUM;
		}
		return null;
	}
	
	public int getIndex() {
		return ID;
	}
	
	public ItemStack getDrop() {
		return DROP.copy();
	}
	
	public ItemStack getSourceDrop() {
		return SOURCE_DROP.copy();
	}
	
	public int transfer() {
		return TRANSFER;
	}
	
	public int getRed() {
		return CR;
	}
	
	public int getGreen() {
		return CG;
	}
	
	public int getBlue() {
		return CB;
	}
}
