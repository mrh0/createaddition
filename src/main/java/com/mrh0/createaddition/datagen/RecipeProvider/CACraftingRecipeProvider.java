package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.recipes.RecipeBuilder.getDefaultRecipeId;

public class CACraftingRecipeProvider extends RecipeProvider {
    public CACraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected static void StorageRecipe(SaveUtility saveUtility, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, @Nullable String packedGroup, String unpackedName, @Nullable String unpackedGroup, String modId) {
        saveUtility.saveToFolder(
                ShapelessRecipeBuilder.shapeless(unpackedCategory, unpacked, 9).requires(packed).group(unpackedGroup).unlockedBy(getHasName(packed), has(packed)),
                "crafting",
                unpackedName,
                modId
        );
//        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modId,unpackedName));
        saveUtility.saveToFolder(
                ShapedRecipeBuilder.shaped(packedCategory, packed).define('#', unpacked).pattern("###").pattern("###").pattern("###").group(packedGroup).unlockedBy(getHasName(unpacked), has(unpacked)),
                "crafting",
                packedName,
                modId
                );
    }

    protected void StorageRecipe(SaveUtility saveUtility, RecipeCategory unpackedCategory, ItemLike unpacked, RecipeCategory packedCategory, ItemLike packed, String packedName, String unpackedName, String modid) {
        StorageRecipe(saveUtility, unpackedCategory, unpacked, packedCategory, packed, packedName, (String) null, unpackedName, (String) null, modid);
    }

    protected void makeSpoolRecipe(SaveUtility saveUtility, TagKey<Item> WireType, ItemLike SpoolOutput) {
        saveUtility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE,SpoolOutput)
                        .pattern(" W ")
                        .pattern("WSW")
                        .pattern(" W ")
                        .define('W', WireType)
                        .define('S', CAItems.SPOOL)
                        .unlockedBy("has_wire", has(WireType))
                        .unlockedBy("has_spool", has(CAItems.SPOOL))
                        .unlockedBy("has_wired_spool", has(SpoolOutput))
        );
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SaveUtility utility = new SaveUtility(recipeOutput);
        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CABlocks.BARBED_WIRE.asItem(), 2)
                    .pattern(" W ")
                    .pattern("W W")
                    .pattern(" W ")
                    .define('W', CATagRegister.Items.IRON_WIRES)
                    .unlockedBy("has_iron_wires", has(CATagRegister.Items.IRON_WIRES))
                    .unlockedBy("has_barbed_wires", has(CABlocks.BARBED_WIRE.asItem()))
        );


        StorageRecipe(
                utility,
                RecipeCategory.MISC,
                CAItems.BIOMASS_PELLET.asItem(),
                RecipeCategory.BUILDING_BLOCKS,
                CABlocks.BIOMASS_PALLET.asItem(),
                "biomass_pallet_block",
                "biomass_pellet_from_biomass_pallet_block", CreateAddition.MODID);

        //Capacitor recipes
        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CAItems.CAPACITOR.asItem())
                        .pattern("Z")
                        .pattern("C")
                        .pattern("T")
                        .define('Z', CATagRegister.Items.ZINC_PLATES)
                        .define('C', CATagRegister.Items.commonTags("plates/copper"))
                        .define('T', Items.REDSTONE_TORCH)
                        .unlockedBy("has_zinc_plate", has(CATagRegister.Items.ZINC_PLATES))
                        .unlockedBy("has_copper_plate", has(CATagRegister.Items.commonTags("plates/copper")))
                        .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                        .unlockedBy("has_capacitor", has(CAItems.CAPACITOR)),
                "capacitor_1"
        );
        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CAItems.CAPACITOR.asItem())
                        .pattern("C")
                        .pattern("Z")
                        .pattern("T")
                        .define('Z', CATagRegister.Items.ZINC_PLATES)
                        .define('C', CATagRegister.Items.commonTags("plates/copper"))
                        .define('T', Items.REDSTONE_TORCH)
                        .unlockedBy("has_zinc_plate", has(CATagRegister.Items.ZINC_PLATES))
                        .unlockedBy("has_copper_plate", has(CATagRegister.Items.commonTags("plates/copper")))
                        .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                        .unlockedBy("has_capacitor", has(CAItems.CAPACITOR)),
                "capacitor_2"
        );

        makeSpoolRecipe(utility, CATagRegister.Items.COPPER_WIRES, CAItems.COPPER_SPOOL.asItem());
        makeSpoolRecipe(utility, CATagRegister.Items.GOLD_WIRES, CAItems.GOLD_SPOOL.asItem());
        makeSpoolRecipe(utility, CATagRegister.Items.ELECTRUM_WIRES, CAItems.ELECTRUM_SPOOL.asItem());

        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CAItems.DIAMOND_GRIT_SANDPAPER.asItem())
                        .requires(Items.PAPER)
                        .requires(CATagRegister.Items.DIAMOND_DUSTS)
                        .unlockedBy("has_paper", has(Items.PAPER))
                        .unlockedBy("has_diamond_dust", has(CATagRegister.Items.DIAMOND_DUSTS))
        );

        new SaveUtility(recipeOutput.withConditions(
                new ModLoadedCondition("computercraft")
        )).saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CABlocks.DIGITAL_ADAPTER.asItem())
                        .requires(ItemTags.create(ResourceLocation.fromNamespaceAndPath("computercraft","wired_modem")))
                        .requires(CATagRegister.Items.commonTags("plates/brass"))
                        .requires(Items.REDSTONE_TORCH)
                        .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                        .unlockedBy("has_brass_plate", has(CATagRegister.Items.commonTags("plates/brass")))
                        .unlockedBy("has_digital_adapter", has(CABlocks.DIGITAL_ADAPTER.asItem()))

        );

        StorageRecipe(
                utility,
                RecipeCategory.MISC,
                CAItems.ELECTRUM_NUGGET.asItem(),
                RecipeCategory.MISC,
                CAItems.ELECTRUM_INGOT.asItem(),
                "electrum_ingot_from_nugget",
                "electrum_nugget",
                CreateAddition.MODID
        );
        StorageRecipe(
                utility,
                RecipeCategory.MISC,
                CAItems.ELECTRUM_INGOT.asItem(),
                RecipeCategory.BUILDING_BLOCKS,
                CABlocks.ELECTRUM_BLOCK.asItem(),
                "electrum_block",
                "electrum_ingot_from_electrum_block",
                CreateAddition.MODID
        );

        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, CAItems.ELECTRUM_AMULET.asItem())
                        .pattern(" WW")
                        .pattern("EEW")
                        .pattern("GE ")
                        .define('W', CATagRegister.Items.ELECTRUM_WIRES)
                        .define('E', CATagRegister.Items.ELECTRUM_INGOTS)
                        .define('G', Tags.Items.GEMS_EMERALD)
                        .unlockedBy("has_electrum_wire", has(CATagRegister.Items.ELECTRUM_WIRES))
                        .unlockedBy("has_electrum_ingot", has(CATagRegister.Items.ELECTRUM_INGOTS))
                        .unlockedBy("has_emerald", has(Tags.Items.GEMS_EMERALD))
                        .unlockedBy("has_electrum_amulet", has(CAItems.ELECTRUM_AMULET))
        );

        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CAItems.FESTIVE_SPOOL)
                        .requires(CAItems.COPPER_SPOOL)
                        .requires(Tags.Items.DUSTS_REDSTONE)
                        .requires(CAItems.BIOMASS)
                        .unlockedBy("has_copper_spool", has(CAItems.COPPER_SPOOL))
                        .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                        .unlockedBy("has_biomass", has(CAItems.BIOMASS))
                        .unlockedBy("has_festive_spool", has(CAItems.FESTIVE_SPOOL))
        );

        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CABlocks.LARGE_CONNECTOR.asItem(),2)
                        .requires(CATagRegister.Items.LARGE_CONNECTOR_USABLE_RODS)
                        .requires(AllItems.ANDESITE_ALLOY.asItem())
                        .requires(AllItems.ANDESITE_ALLOY.asItem())
                        .requires(Tags.Items.SLIME_BALLS)
                        .unlockedBy("has_rod", has(CATagRegister.Items.LARGE_CONNECTOR_USABLE_RODS))
                        .unlockedBy("has_andesite_alloy", has(AllItems.ANDESITE_ALLOY.asItem()))
                        .unlockedBy("has_slime_balls", has(Tags.Items.SLIME_BALLS))
                        .unlockedBy("has_large_connector", has(CABlocks.LARGE_CONNECTOR.asItem()))
        );

        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, CABlocks.MODULAR_ACCUMULATOR.asItem())
                        .pattern(" R ")
                        .pattern("CBC")
                        .pattern(" W ")
                        .define('R', CATagRegister.Items.COPPER_RODS)
                        .define('B', AllBlocks.BRASS_CASING.asItem())
                        .define('C', CAItems.CAPACITOR.asItem())
                        .define('W', CATagRegister.Items.ELECTRUM_WIRES)
                        .unlockedBy("has_copper_rods", has(CATagRegister.Items.COPPER_RODS))
                        .unlockedBy("has_brass_casing", has(AllBlocks.BRASS_CASING.asItem()))
                        .unlockedBy("has_capacitor", has(CAItems.CAPACITOR.asItem()))
                        .unlockedBy("has_wires", has(CATagRegister.Items.MODULAR_ACCUMULATOR_USABLE_WIRES))
                        .unlockedBy("has_modular_accumulator", has(CABlocks.MODULAR_ACCUMULATOR.asItem()))
        );

        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, CABlocks.PORTABLE_ENERGY_INTERFACE.asItem())
                        .requires(AllBlocks.BRASS_CASING.asItem())
                        .requires(AllBlocks.CHUTE.asItem())
                        .requires(CAItems.COPPER_SPOOL.asItem())
                        .unlockedBy("has_brass_casing", has(AllBlocks.BRASS_CASING.asItem()))
                        .unlockedBy("has_chute", has(AllBlocks.CHUTE.asItem()))
                        .unlockedBy("has_copper_spool", has(CAItems.COPPER_SPOOL.asItem()))
                        .unlockedBy("has_portable_energy_interface", has(CABlocks.PORTABLE_ENERGY_INTERFACE.asItem()))
        );

        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, CABlocks.REDSTONE_RELAY.asItem())
                        .pattern(" R ")
                        .pattern("CEC")
                        .pattern("SSS")
                        .define('R', Tags.Items.DUSTS_REDSTONE)
                        .define('S', Tags.Items.STONES)
                        .define('C', CABlocks.SMALL_CONNECTOR.asItem())
                        .define('E', AllItems.ELECTRON_TUBE.asItem())
                        .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                        .unlockedBy("has_stone", has(Tags.Items.STONES))
                        .unlockedBy("has_connector", has(CABlocks.SMALL_CONNECTOR.asItem()))
                        .unlockedBy("has_electorn_tube", has(AllItems.ELECTRON_TUBE.asItem()))
                        .unlockedBy("has_redstone_relay", has(CABlocks.REDSTONE_RELAY.asItem()))
        );

        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CABlocks.ROLLING_MILL.asItem())
                        .pattern("PSP")
                        .pattern("ASA")
                        .pattern("ACA")
                        .define('P', CATagRegister.Items.commonTags("plates/iron"))
                        .define('S', AllBlocks.SHAFT.asItem())
                        .define('A', AllItems.ANDESITE_ALLOY.asItem())
                        .define('C', AllBlocks.ANDESITE_CASING)
                        .unlockedBy("has_iron_plates", has(CATagRegister.Items.commonTags("plates/iron")))
                        .unlockedBy("has_shaft", has(AllBlocks.SHAFT.asItem()))
                        .unlockedBy("has_andesite_alloy", has(AllItems.ANDESITE_ALLOY.asItem()))
                        .unlockedBy("has_andesite_casing", has(AllBlocks.ANDESITE_CASING))
                        .unlockedBy("has_rolling_mill", has(CABlocks.ROLLING_MILL.asItem()))
        );

        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CABlocks.SMALL_CONNECTOR.asItem(),3)
                        .requires(CATagRegister.Items.COPPER_RODS)
                        .requires(AllItems.ANDESITE_ALLOY.asItem())
                        .requires(Tags.Items.SLIME_BALLS)
                        .unlockedBy("has_small_connector", has(CABlocks.SMALL_CONNECTOR.asItem()))
                        .unlockedBy("has_copper_rod", has(CATagRegister.Items.COPPER_RODS))
                        .unlockedBy("has_andesite_alloy", has(AllItems.ANDESITE_ALLOY.asItem()))
                        .unlockedBy("has_slime_ball", has(Tags.Items.SLIME_BALLS))
        );
        utility.saveToCraftingFolder(
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, CABlocks.SMALL_LIGHT_CONNECTOR.asItem())
                        .requires(CATagRegister.Items.IRON_WIRES)
                        .requires(Tags.Items.GLASS_BLOCKS)
                        .requires(CABlocks.SMALL_CONNECTOR.asItem())
                        .unlockedBy("has_small_light_connector", has(CABlocks.SMALL_LIGHT_CONNECTOR.asItem()))
                        .unlockedBy("has_iron_wire", has(CATagRegister.Items.IRON_WIRES))
                        .unlockedBy("has_glass_block", has(Tags.Items.GLASS_BLOCKS))
                        .unlockedBy("has_small_connector", has(CABlocks.SMALL_CONNECTOR.asItem()))
        );

        /*
        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CAItems.SPOOL.asItem(), 16)
                        .pattern("P")
                        .pattern("N")
                        .pattern("P")
                        .define('P', CATagRegister.Items.commonTags("plates/iron"))
                        .define('N', Tags.Items.NUGGETS_IRON)
                        .unlockedBy("has_spool", has(CAItems.SPOOL.asItem()))
                        .unlockedBy("has_iron_plate", has(CATagRegister.Items.commonTags("plates/iron")))
                        .unlockedBy("has_iron_nugget", has(Tags.Items.NUGGETS_IRON))
        );
        */
        utility.saveToCraftingFolder(
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CAItems.SPOOL.asItem(), 24)
                        .pattern("P")
                        .pattern("N")
                        .pattern("P")
                        .define('P', CATagRegister.Items.commonTags("plates/iron"))
                        .define('N', CATagRegister.Items.RODS_IRON)
                        .unlockedBy("has_spool", has(CAItems.SPOOL.asItem()))
                        .unlockedBy("has_iron_plate", has(CATagRegister.Items.commonTags("plates/iron")))
                        .unlockedBy("has_iron_rod", has(CATagRegister.Items.RODS))
        );

        SimpleCookingRecipeBuilder.smoking(
                Ingredient.of(new ItemLike[] {CAItems.CAKE_BASE.asItem()}),
                RecipeCategory.FOOD,
                CAItems.CAKE_BASE_BAKED,
                0.0f,
                100
                )
                .unlockedBy("has_cake_base", has(CAItems.CAKE_BASE))
                .unlockedBy("has_cake_base_baked", has(CAItems.CAKE_BASE_BAKED))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"smoking/cake_base_baked"));
    }

    public class SaveUtility{
        public RecipeOutput output;

        public SaveUtility(RecipeOutput output) {
            this.output = output;
        }

        private void saveToCraftingFolder(RecipeBuilder recipeBuilder) {
            saveToFolder(recipeBuilder, "crafting");
        }

        private void saveToCraftingFolder(RecipeBuilder recipeBuilder, String fileName) {
            saveToFolder(recipeBuilder, "crafting", fileName);
        }

        private void saveToFolder( RecipeBuilder recipeBuilder, String folderName) {
            saveToFolder(recipeBuilder, folderName, getDefaultRecipeId(recipeBuilder.getResult()).getPath());
        }

        private void saveToFolder( RecipeBuilder recipeBuilder, String folderName, String fileName) {
            saveToFolder(recipeBuilder, folderName, fileName, CreateAddition.MODID);
        }

        private void saveToFolder( RecipeBuilder recipeBuilder, String folderName, String fileName, String modId) {
            saveToPath(recipeBuilder, String.format("%s/%s", folderName,fileName), modId);
        }

        private void saveToPath( RecipeBuilder recipeBuilder, String path, String modId) {
            recipeBuilder.save(this.output, ResourceLocation.fromNamespaceAndPath(modId, path));
        }

        private void saveToPath( RecipeBuilder recipeBuilder, String path) {
            saveToPath(recipeBuilder, path, CreateAddition.MODID);
        }

    }

}
