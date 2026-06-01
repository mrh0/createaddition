package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.concurrent.CompletableFuture;

public class CAMixingRecipeGen extends MixingRecipeGen {

    public CAMixingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateAddition.MODID);
    }

    private GeneratedRecipe makeBiomassRecipe(String recipeName, ItemLike itemLike, int amount) {
        return create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, recipeName), b -> {
            b.require(CATagRegister.Fluids.PLANTOIL,100)
                    .output(CAItems.BIOMASS)
                    .requiresHeat(HeatCondition.HEATED);
            for(int i=0;i<amount;i++) b.require(itemLike);
            return b;
        });
    }

    private GeneratedRecipe makeBiomassRecipe(String recipeName, TagKey<Item> tag, int amount) {
        return create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, recipeName), b -> {
            b.require(CATagRegister.Fluids.PLANTOIL, 100)
                    .output(CAItems.BIOMASS)
                    .requiresHeat(HeatCondition.HEATED);
            for(int i=0;i<amount;i++) b.require(tag);
            return b;
        });

    }

    GeneratedRecipe
    BIO_ETHANOL = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"bioethanol"), b -> b.require(Items.SUGAR)
            .require(AllItems.CINDER_FLOUR)
            .require(CAItems.BIOMASS)
            .require(CAItems.BIOMASS)
            .output(CAFluids.BIOETHANOL.getSource().getSource(),125)
    ),
    BIOMASS_FROM_CROPS = makeBiomassRecipe("biomass_from_crops", Tags.Items.CROPS, 2),
    BIOMASS_FROM_FLOWERS = makeBiomassRecipe("biomass_from_flowers", ItemTags.FLOWERS, 2),
    BIOMASS_FROM_HONEYCOMB = makeBiomassRecipe("biomass_from_honeycomb", Items.HONEYCOMB, 1),
    BIOMASS_FROM_LEAVES = makeBiomassRecipe("biomass_from_leaves", ItemTags.LEAVES, 3),
    BIOMASS_FROM_PLANT_FOODS = makeBiomassRecipe("biomass_from_plant_foods", CATagRegister.Items.PLANT_FOODS, 2),
    BIOMASS_FROM_PLANTS = makeBiomassRecipe("biomass_from_plants", CATagRegister.Items.PLANTS, 3),
    BIOMASS_FROM_SAPLINGS = makeBiomassRecipe("biomass_from_saplings", ItemTags.SAPLINGS, 3),
    BIOMASS_FROM_STRICKS = makeBiomassRecipe("biomass_from_stricks", Items.STICK, 8),
    ELECTRUM = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"electrum"), b -> b.requiresHeat(HeatCondition.HEATED)
            .require(CATagRegister.Items.commonTags("ingots","gold"))
            .require(CATagRegister.Items.commonTags("ingots","silver"))
            .output(CAItems.ELECTRUM_INGOT,2)
            .withCondition(new NotCondition(
                    new TagEmptyCondition(CATagRegister.Items.commonTags("ingots", "silver"))
            ))
    ),
    NETHERRACK = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"netherrack"), b -> b
            .require(Tags.Items.COBBLESTONES)
            .require(AllItems.CINDER_FLOUR)
            .require(FluidTags.LAVA, 25)
            .output(Items.NETHERRACK)
    )

    ;
}
