package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CAItemProperties {
	public static void register() {
		/*ItemProperties.register(CAItems.OVERCHARGED_HAMMER.get(), new ResourceLocation(CreateAddition.MODID, "charged"), new ItemPropertyFunction() {
			@Override
			public float call(ItemStack item, ClientLevel world, LivingEntity entity) {
				return 0;
			}
		});*/
	}
}
