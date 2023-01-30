package com.mrh0.createaddition.blocks.modular_accumulator;

import java.util.List;

import com.mrh0.createaddition.config.Config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ModularAccumulatorBlockItem extends BlockItem {
	public ModularAccumulatorBlockItem(Block block, Properties props) {
		super(block, props);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}
	
	private static int getOrCreateEnergy(ItemStack stack) {
		var tag = stack.getTag();
		if(tag == null)
			tag = new CompoundTag();
		if(!tag.contains("energy", Tag.TAG_STRING))
			tag.putInt("energy", 0);
		return tag.getInt("energy");
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
	}
	
	
}
