package com.mrh0.createaddition.item;

import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class DevTool extends Item {

	public DevTool(Properties props) {
		super(props);
	}

	
	public static boolean hasPos(CompoundNBT nbt) {
		if(nbt == null)
			return false;
    	return nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("node");
    }
	
	public static BlockPos getPos(CompoundNBT nbt){
		if(nbt == null)
			return null;
    	return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }
	
	public static void clearPos(CompoundNBT nbt){
    	nbt.remove("x");
    	nbt.remove("y");
    	nbt.remove("z");
    	nbt.remove("node");
    }
	
	public static CompoundNBT setContent(CompoundNBT nbt, BlockPos pos, int node){
		if(nbt == null)
			return new CompoundNBT();
    	nbt.putInt("x", pos.getX());
    	nbt.putInt("y", pos.getY());
    	nbt.putInt("z", pos.getZ());
    	nbt.putInt("node", node);
    	return nbt;
    }
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	CompoundNBT nbt = stack.getTag();
    	super.appendHoverText(stack, worldIn, tooltip, flagIn);
    	if(hasPos(nbt))
    		tooltip.add(new TranslationTextComponent("item."+CreateAddition.MODID+".devtool.tooltip"));
    }
	
}
