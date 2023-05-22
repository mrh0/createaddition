package com.mrh0.createaddition.recipe.rolling;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RollingRecipeInfo implements IRecipeTypeInfo {

    private ResourceLocation id;
    private SequencedAssemblyRollingRecipeSerializer serializer;
    private RecipeType<RollingRecipe> type;

    public RollingRecipeInfo(ResourceLocation id, SequencedAssemblyRollingRecipeSerializer serializer, RecipeType<RollingRecipe> type) {
        this.id = id;
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type;
    }
}
