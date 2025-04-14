package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class CACreativeModeTabs {
    private static final LazyRegistrar<CreativeModeTab> TAB_REGISTER =
            LazyRegistrar.create(Registries.CREATIVE_MODE_TAB, CreateAddition.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TAB_REGISTER.register("main",
            () -> FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.createaddition.main"))
                    .icon(CABlocks.ELECTRIC_MOTOR::asStack)
                    .displayItems(new RegistrateDisplayItemsGenerator())
                    .build());

    public static void register() {
        TAB_REGISTER.register();
    }

    public static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        private List<Item> collectBlocks(RegistryObject<CreativeModeTab> tab, Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Block> entry : CreateAddition.REGISTRATE.getAll(Registries.BLOCK)) {
                if (!CreateAddition.REGISTRATE.isInCreativeTab(entry, tab.getKey()))
                    continue;
                Item item = entry.get()
                        .asItem();
                if (item == Items.AIR)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
            return items;
        }

        private List<Item> collectItems(RegistryObject<CreativeModeTab> tab, Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();


            for (RegistryEntry<Item> entry : CreateAddition.REGISTRATE.getAll(Registries.ITEM)) {
                if (!CreateAddition.REGISTRATE.isInCreativeTab(entry, tab.getKey()))
                    continue;
                Item item = entry.get();
                if (item instanceof BlockItem)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            return items;
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items) {
            for (Item item : items) {
                output.accept(item);
            }
        }

        List<Item> exclude = List.of(CAItems.CAKE_BASE.get(), CAItems.CAKE_BASE_BAKED.get());

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            List<Item> items = new LinkedList<>();
            items.addAll(collectBlocks(MAIN_TAB, (item) -> {
                if (item == CABlocks.DIGITAL_ADAPTER.asItem()) return !CreateAddition.CC_ACTIVE;
                return false;
            }));
            items.addAll(collectItems(MAIN_TAB, (item) -> exclude.contains(item)));

            outputAll(output, items);
        }
    }
}
