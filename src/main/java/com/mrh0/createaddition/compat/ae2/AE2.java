package com.mrh0.createaddition.compat.ae2;

import appeng.core.definitions.AEItems;
import net.minecraft.world.item.ItemStack;

public class AE2 {
	public static boolean isCertusQuartz(ItemStack stack) {
		return AEItems.CERTUS_QUARTZ_CRYSTAL.isSameAs(stack);//AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().asItem() == stack.getItem();
	}
	
	public static ItemStack getChargedCertusQuartz(int count) {
		return new ItemStack(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem().asItem(), count);//Api.instance().definitions().materials().certusQuartzCrystalCharged().maybeStack(count);
	}
}
