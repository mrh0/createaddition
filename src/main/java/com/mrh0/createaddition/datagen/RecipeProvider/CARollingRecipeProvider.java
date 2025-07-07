package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.RecipeBuilders.CARollingRecipeBuilder;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CARollingRecipeProvider extends StandardProcessingRecipeGen<RollingRecipe> {
    public static final IRecipeTypeInfo recipeType = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CARecipes.ROLLING.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CARecipes.ROLLING.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CARecipes.ROLLING_TYPE.get();
        }
    };

    private final HolderLookup.Provider provider;

    public CARollingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAddition.MODID);
        try {
            this.provider = registries.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void metalRolling(@NotNull RecipeOutput output, Item rod, Item wire, String metal) {
        CARollingRecipeBuilder.rolling(rod, 2)
                .require(AllTags.commonItemTag("ingots/" + metal))
                .save(output, metal + "_ingot");

        CARollingRecipeBuilder.rolling(wire, 2)
                .require(AllTags.commonItemTag("plates/" + metal))
                .save(output, metal + "_plate");
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        metalRolling(output, CAItems.COPPER_ROD.get(), CAItems.COPPER_WIRE.get(), "copper");
        metalRolling(output, CAItems.ELECTRUM_ROD.get(), CAItems.ELECTRUM_WIRE.get(), "electrum");
        metalRolling(output, CAItems.GOLD_ROD.get(), CAItems.GOLD_WIRE.get(), "gold");
        metalRolling(output, CAItems.IRON_ROD.get(), CAItems.IRON_WIRE.get(), "iron");

        CARollingRecipeBuilder.rolling(CAItems.BRASS_ROD.get(), 2)
                .require(AllTags.commonItemTag("ingots/brass"))
                .save(output, "brass_ingot");

        CARollingRecipeBuilder.rolling(CAItems.STRAW)
                .require(Items.BAMBOO)
                .save(output);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
       return recipeType;
    }
}
