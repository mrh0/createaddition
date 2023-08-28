package com.mrh0.createaddition.groups;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class ModGroup {
	public static final CreativeModeTab MAIN= FabricItemGroup.builder().icon(ModGroup::makeIcon).title(Component.translatable("itemGroup.createaddition.main")).build();
	public static final ResourceKey<CreativeModeTab> MAIN_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(CreateAddition.MODID,"main"));
	public static ItemStack makeIcon() {
		return new ItemStack(CABlocks.ELECTRIC_MOTOR.get());
	}


	public static void register(){

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN_KEY, MAIN);
		ItemGroupEvents.modifyEntriesEvent(MAIN_KEY).register(content -> {
			CreateAddition.REGISTRATE.getAll(Registries.ITEM).forEach(entry->{
				content.accept(entry.get());
			});
		});
	}
}
