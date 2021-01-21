package com.mrh0.createaddition.item;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class Multimeter extends Item {

	public Multimeter(Properties props) {
		super(props);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext c) {
		TileEntity te = c.getWorld().getTileEntity(c.getPos());
		if(te != null && !c.getWorld().isRemote()) {
			LazyOptional<IEnergyStorage> cap;
			cap = te.getCapability(CapabilityEnergy.ENERGY, c.getFace());
			if(cap != null) {
				c.getPlayer().sendMessage(new TranslationTextComponent("item."+CreateAddition.MODID+".multimeter.title").append(new StringTextComponent(" " +getString(cap.orElse(null)) + "fe")),
						PlayerEntity.getUUID(c.getPlayer().getGameProfile()));
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	
	public static String getString(IEnergyStorage ies) {
		if(ies == null)
			return "NaN";
		return format(ies.getEnergyStored()) + "/" + format(ies.getMaxEnergyStored());
	}
	
	public static String format(int n) {
		if(n > 1000000)
			return Math.round((double)n/100000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}
}
