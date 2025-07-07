package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.RecipeBuilders.CALiquidBurningRecipeBuilder;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.conditions.HasFluidTagCondition;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CALiquidBurningRecipeProvider extends StandardProcessingRecipeGen<LiquidBurningRecipe> {
    public static final IRecipeTypeInfo recipeType = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CARecipes.LIQUID_BURNING.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CARecipes.LIQUID_BURNING.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CARecipes.LIQUID_BURNING_TYPE.get();
        }
    };

    private final HolderLookup.Provider provider;

    public CALiquidBurningRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAddition.MODID);
        try {
            this.provider = registries.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        CALiquidBurningRecipeBuilder.liquidBurning(24000)
                .require(CATagRegister.Fluids.BIOFUEL)
                .superheated()
                .save(output, "biofuel");
        CALiquidBurningRecipeBuilder.liquidBurning(4800)
                .require(CATagRegister.Fluids.PLANTOIL)
                .save(output, "plantoil");
        CALiquidBurningRecipeBuilder.liquidBurning(20000)
                .require(FluidTags.LAVA)
                .save(output, "lava");

        CALiquidBurningRecipeBuilder.liquidBurning(24000)
                .require(CATagRegister.Fluids.BIODIESEL)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.BIODIESEL)), "biodiesel");
        CALiquidBurningRecipeBuilder.liquidBurning(4800)
                .require(CATagRegister.Fluids.CREOSOTE)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.CREOSOTE)), "creosote");
        CALiquidBurningRecipeBuilder.liquidBurning(9600)
                .require(CATagRegister.Fluids.CRUDE_OIL)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.CRUDE_OIL)), "crude_oil");
        CALiquidBurningRecipeBuilder.liquidBurning(24000)
                .require(CATagRegister.Fluids.DIESEL)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.DIESEL)), "diesel");
        CALiquidBurningRecipeBuilder.liquidBurning(8000)
                .require(CATagRegister.Fluids.ETHANOL)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.ETHANOL)), "ethanol");
        CALiquidBurningRecipeBuilder.liquidBurning(24000)
                .require(CATagRegister.Fluids.GASOLINE)
                .save(output.withConditions(new HasFluidTagCondition(CATagRegister.Fluids.GASOLINE)), "gasoline");
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
       return recipeType;
    }
}
