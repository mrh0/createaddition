package com.mrh0.createaddition.recipe.rolling;

import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RollingRecipeInfo implements IRecipeTypeInfo {

    private ResourceLocation id;
    private RecipeSerializer<?> serializer;
    private RecipeType<RollingRecipe> type;

    public RollingRecipeInfo(ResourceLocation id, RecipeSerializer<?> serializer, RecipeType<RollingRecipe> type) {
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
        return null;
    }

    @Override
    public <T extends RecipeType<?>> T getType() {
        return null;
    }
}
