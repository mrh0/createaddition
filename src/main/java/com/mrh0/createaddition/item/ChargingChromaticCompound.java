package com.mrh0.createaddition.item;

import com.mrh0.createaddition.config.Config;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class ChargingChromaticCompound extends Item {

	public static int MAX_CHARGE = Config.OVERCHARGING_ENERGY_COST.get();
	
	public ChargingChromaticCompound(Properties props) {
		super(props);
	}
	
	/*@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1d - getCharge(stack);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}*/
	
	@Override
	public boolean isBarVisible(ItemStack p_150899_) {
		return true;
	}
	
	public static boolean hasKey(CompoundTag nbt){
		if(nbt == null)
			return false;
    	return nbt.contains("charge");
    }
	
	public static int getEnergy(CompoundTag nbt){
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
		CompoundTag nbt = new CompoundTag();
		int c = getEnergy(stack.getTag());
		int n = Math.min(c + in, MAX_CHARGE);
		nbt.putInt("charge", n);
		stack.setTag(null);
		stack.setTag(nbt);
		return n - c;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
		if (group != CreativeModeTab.TAB_SEARCH)
			return;
		super.fillItemCategory(group, list);
	}
}
