package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CAItemProperties {
	public static void register() {
		ItemModelsProperties.register(CAItems.OVERCHARGED_HAMMER.get(), new ResourceLocation(CreateAddition.MODID, "charged"), new IItemPropertyGetter() {
			@Override
			public float call(ItemStack item, ClientWorld world, LivingEntity entity) {
				return 0;
			}
		});
	}
}
