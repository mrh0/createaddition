package com.mrh0.createaddition.datagen.TagProvider;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CATagRegister {
    public static class Items {
        public static final TagKey<Item> DIAMOND_DUSTS = commonTags("dusts","diamond");
        public static final TagKey<Item> OBSIDIAN_DUSTS = commonTags("dusts","obsidian");
        public static final TagKey<Item> BIO_FUELS = commonTags("fuels","bio");
        public static final TagKey<Item> ELECTRUM_BLOCKS = commonTags("blocks","electrum");
        public static final TagKey<Item> ELECTRUM_INGOTS = commonTags("ingots","electrum");
        public static final TagKey<Item> ELECTRUM_NUGGETS = commonTags("nuggets","electrum");
        public static final TagKey<Item> ELECTRUM_PLATES = commonTags("plates","electrum");
        public static final TagKey<Item> ZINC_PLATES = commonTags("plates","zinc");
        public static final TagKey<Item> ZINC_INGOTS = commonTags("ingots","zinc");
        public static final TagKey<Item> ALL_METAL_RODS = commonTags("rods","all_metal");
        public static final TagKey<Item> BRASS_RODS = commonTags("rods","brass");
        public static final TagKey<Item> COPPER_RODS = commonTags("rods","copper");
        public static final TagKey<Item> ELECTRUM_RODS = commonTags("rods","electrum");
        public static final TagKey<Item> GOLD_RODS = commonTags("rods","gold");
        public static final TagKey<Item> IRON_RODS = commonTags("rods","iron");
        public static final TagKey<Item> ALL_METAL_WIRES = commonTags("wires","all_metal");
        public static final TagKey<Item> COPPER_WIRES = commonTags("wires","copper");
        public static final TagKey<Item> ELECTRUM_WIRES = commonTags("wires","electrum");
        public static final TagKey<Item> GOLD_WIRES = commonTags("wires","gold");
        public static final TagKey<Item> IRON_WIRES = commonTags("wires","iron");
        public static final TagKey<Item> BUCKETS = commonTags("buckets");

        public static final TagKey<Item> DUSTS = commonTags("dusts");
        public static final TagKey<Item> FUELS = commonTags("fuels");
        public static final TagKey<Item> INGOTS = commonTags("ingots");
        public static final TagKey<Item> NUGGETS = commonTags("nuggets");
        public static final TagKey<Item> PLATES = commonTags("plates");
        public static final TagKey<Item> RODS = commonTags("rods");
        public static final TagKey<Item> WIRES = commonTags("wires");

        public static final TagKey<Item> FOODS = commonTags("foods");
        public static final TagKey<Item> FOODS_EDIBLE = commonTags("foods", "edible_when_placed");

        public static final TagKey<Item> STORAGE_BLOCKS_BIOMASS = commonTags("storage_blocks","biomass");
        public static final TagKey<Item> STORAGE_BLOCKS_BIO = commonTags("storage_blocks","bio");
        public static final TagKey<Item> STORAGE_BLOCKS_ELECTRUM = commonTags("storage_blocks","electrum");

        public static final TagKey<Item> PLANT_FOODS = createAdditionsTags("plant_foods");
        public static final TagKey<Item> PLANTS = createAdditionsTags("plants");
        public static final TagKey<Item> SPOOLS = createAdditionsTags("spools");

        public static final TagKey<Item> LARGE_CONNECTOR_USABLE_RODS = createAdditionsTags("large_connector_usable_rods");
        public static final TagKey<Item> MODULAR_ACCUMULATOR_USABLE_WIRES = createAdditionsTags("modular_accumulator_usable_wires");

        public static TagKey<Item> commonTags(String folder, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Item> commonTags(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Item> createAdditionsTags(String folder, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, String.format("%s/%s", folder, name)));
        }

        public static TagKey<Item> createAdditionsTags(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, name));
        }

        public static TagKey<Item> modTags(String mod, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(mod, name));
        }

        public static TagKey<Item> modTags(String mod, String folder, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(mod, String.format("%s/%s", folder, name)));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> STORAGE_BLOCKS = commonTags("storage_blocks");
        public static final TagKey<Block> STORAGE_BLOCKS_BIOMASS = commonTags("storage_blocks","biomass");
        public static final TagKey<Block> STORAGE_BLOCKS_BIO = commonTags("storage_blocks","bio");
        public static final TagKey<Block> STORAGE_BLOCKS_ELECTRUM = commonTags("storage_blocks","electrum");

        public static final TagKey<Block> FARMERSDELIGHT_MINEABLE_KNIFE = modTags("farmersdelight", "mineable", "knife");

        public static TagKey<Block> commonTags(String folder, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Block> commonTags(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Block> modTags(String mod, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(mod, name));
        }

        public static TagKey<Block> modTags(String mod, String folder, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(mod, String.format("%s/%s", folder, name)));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> BIOFUEL = commonTags("biofuel");
        public static final TagKey<Fluid> CREOSOTE = commonTags("creosote");
        public static final TagKey<Fluid> CRUDE_OIL = commonTags("crude_oil");
        public static final TagKey<Fluid> PLANTOIL = commonTags("plantoil");
        public static final TagKey<Fluid> ETHANOL = commonTags("ethanol");
        public static final TagKey<Fluid> GASOLINE = commonTags("gasoline");
        public static final TagKey<Fluid> DIESEL = commonTags("diesel");
        public static final TagKey<Fluid> BIODIESEL = commonTags("biodiesel");
        public static final TagKey<Fluid> IGNITES = commonTags("ignites");

        public static TagKey<Fluid> commonTags(String folder, String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", String.format("%s/%s", folder, name)));
        }

        public static TagKey<Fluid> commonTags(String name) {
            return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
