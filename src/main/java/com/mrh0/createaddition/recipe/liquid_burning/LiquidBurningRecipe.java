package com.mrh0.createaddition.recipe.liquid_burning;

import com.mojang.serialization.MapCodec;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

public class LiquidBurningRecipe extends ProcessingRecipe<FluidRecipeWrapper, LiquidBurningRecipeParams> {
    public static final IRecipeTypeInfo TYPE_INFO = new IRecipeTypeInfo() {
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
    protected int burnTime;
    protected boolean superheated;

    public LiquidBurningRecipe(LiquidBurningRecipeParams params) {
        super(TYPE_INFO, params);
        burnTime = params.getBurnTime();
        superheated = params.isSuperheated();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public boolean isSuperheated() {
        return superheated;
    }

    @Override
    public boolean matches(@NotNull FluidRecipeWrapper wrapper, @NotNull Level world) {
        if (fluidIngredients == null) return false;
        if (wrapper.fluid == null) return false;
        return getFluidInput().test(wrapper.fluid);
    }

    public SizedFluidIngredient getFluidInput() {
        if (fluidIngredients.isEmpty())
            throw new IllegalStateException("Filling Recipe has no fluid ingredient!");
        return fluidIngredients.get(0);
    }


    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 1;
    }

    @FunctionalInterface
    public interface Factory<R extends LiquidBurningRecipe> extends ProcessingRecipe.Factory<LiquidBurningRecipeParams, R> {
        R create(LiquidBurningRecipeParams params);
    }

    public static class Builder<R extends LiquidBurningRecipe> extends ProcessingRecipeBuilder<LiquidBurningRecipeParams, R, LiquidBurningRecipe.Builder<R>> {
        public Builder(LiquidBurningRecipe.Factory<R> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected LiquidBurningRecipeParams createParams() {
            return new LiquidBurningRecipeParams();
        }

        @Override
        public LiquidBurningRecipe.Builder<R> self() {
            return this;
        }


        public LiquidBurningRecipe.Builder<R> fluid(TagKey<Fluid> fluidTag) {
            return require(SizedFluidIngredient.of(fluidTag, 1000));
        }

        public LiquidBurningRecipe.Builder<R> burnTime(int burnTime) {
            params.burnTime = burnTime;
            return this;
        }

        public LiquidBurningRecipe.Builder<R> superheated() {
            params.superheated = true;
            return this;
        }
    }

    public static class Serializer<R extends LiquidBurningRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(ProcessingRecipe.Factory<LiquidBurningRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, LiquidBurningRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, LiquidBurningRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }
    }

}