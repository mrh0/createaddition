package com.mrh0.createaddition.compat.applied_energistics;

import java.util.Optional;
//import appeng.core.definitions.AEItems;
import net.minecraft.world.item.ItemStack;

public class AE2 {
	public static boolean isCertusQuartz(ItemStack stack) {
		return false;//AEItems.CERTUS_QUARTZ_CRYSTAL.asItem().asItem() == stack.getItem();
	}
	
	public static ItemStack getChargedCertusQuartz(int count) {
		return null;//new ItemStack(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem().asItem(), count);//Api.instance().definitions().materials().certusQuartzCrystalCharged().maybeStack(count);
	}
}
