package com.mrh0.createaddition.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class ChargingChromaticCompound extends Item {

	public static int MAX_CHARGE = 100000;//Config.OVERCHARGING_ENERGY_COST.get();
	
	public ChargingChromaticCompound(Properties props) {
		super(props);
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1d - getCharge(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	public static boolean hasKey(CompoundNBT nbt){
		if(nbt == null)
			return false;
    	return nbt.contains("charge");
    }
	
	public static int getEnergy(CompoundNBT nbt){
		if(nbt == null)
			return 0;
		if(!hasKey(nbt))
			return 0;
    	return nbt.getInt("charge");
    }
	
	public static double getCharge(ItemStack stack) {
		return ((double)getEnergy(stack)) / ((double)MAX_CHARGE);
	}
	
	public static int getEnergy(ItemStack it){
		return getEnergy(it.getTag());
    }
	
	public static int charge(ItemStack stack, int in) {
		CompoundNBT nbt = new CompoundNBT();
		int c = getEnergy(stack.getTag());
		int n = Math.min(c + in, MAX_CHARGE);
		nbt.putInt("charge", n);
		stack.setTag(null);
		stack.setTag(nbt);
		return n - c;
	}
	
	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {
		if (group != ItemGroup.TAB_SEARCH)
			return;
		super.fillItemCategory(group, list);
	}
}
