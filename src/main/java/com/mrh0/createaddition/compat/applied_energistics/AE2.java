package com.mrh0.createaddition.compat.applied_energistics;

import net.minecraft.item.ItemStack;

import java.util.Optional;

import appeng.core.Api;

public class AE2 {
	public static boolean isCertusQuartz(ItemStack stack) {
		return Api.instance().definitions().materials().certusQuartzCrystal().isSameAs(stack);
	}
	
	public static Optional<ItemStack> getChargedCertusQuartz(int count) {
		return Api.instance().definitions().materials().certusQuartzCrystalCharged().maybeStack(count);
	}
}
