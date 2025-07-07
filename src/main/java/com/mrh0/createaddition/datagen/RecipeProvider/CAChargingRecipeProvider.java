package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.RecipeBuilders.ChargingRecipeBuilder;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.block.CopperBlockSet;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CAChargingRecipeProvider extends ProcessingRecipeGen {
    public static final IRecipeTypeInfo recipeType = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CARecipes.CHARGING.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CARecipes.CHARGING.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CARecipes.CHARGING_TYPE.get();
        }
    };

    private final HolderLookup.Provider provider;

    public CAChargingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAddition.MODID);
        try {
            this.provider = registries.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void buildCreateDeoxidizeVariants(CopperBlockSet.Variant<?> variant, @NotNull RecipeOutput output) {
        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.OXIDIZED,false).get(),
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.WEATHERED,false).get()
        ).save(output);
        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.WEATHERED,false).get(),
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.EXPOSED,false).get()
        ).save(output);
        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.EXPOSED,false).get(),
                AllBlocks.COPPER_SHINGLES.get(variant, WeatheringCopper.WeatherState.UNAFFECTED,false).get()
        ).save(output);

        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.OXIDIZED,false).get(),
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.WEATHERED,false).get()
        ).save(output);
        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.WEATHERED,false).get(),
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.EXPOSED,false).get()
        ).save(output);
        ChargingRecipeBuilder.deoxidize(
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.EXPOSED,false).get(),
                AllBlocks.COPPER_TILES.get(variant, WeatheringCopper.WeatherState.UNAFFECTED,false).get()
        ).save(output);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ChargingRecipeBuilder.charging(new ItemStack(Items.ENCHANTED_BOOK), Enchantments.CHANNELING, provider)
                .require(Items.BOOK)
                .maxChargeRate(1000)
                .energy(10000000)
                .save(output, ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, Enchantments.CHANNELING.location().getPath()));

        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_CHISELED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_COPPER_BULB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_COPPER_DOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_COPPER_GRATE).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_COPPER_TRAPDOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_CUT_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_CUT_COPPER_SLAB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.OXIDIZED_CUT_COPPER_STAIRS).save(output);

        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_CHISELED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_COPPER_BULB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_COPPER_DOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_COPPER_GRATE).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_COPPER_TRAPDOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_CUT_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_CUT_COPPER_SLAB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.WEATHERED_CUT_COPPER_STAIRS).save(output);

        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_CHISELED_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_COPPER_BULB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_COPPER_DOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_COPPER_GRATE).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_COPPER_TRAPDOOR).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_CUT_COPPER).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_CUT_COPPER_SLAB).save(output);
        ChargingRecipeBuilder.deoxidize(Blocks.EXPOSED_CUT_COPPER_STAIRS).save(output);

        buildCreateDeoxidizeVariants(CopperBlockSet.SlabVariant.INSTANCE, output);
        buildCreateDeoxidizeVariants(CopperBlockSet.BlockVariant.INSTANCE, output);
        buildCreateDeoxidizeVariants(CopperBlockSet.StairVariant.INSTANCE, output);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
       return recipeType;
    }

    @Override
    protected ProcessingRecipeBuilder getBuilder(ResourceLocation id) {
        return null;
    }
}
