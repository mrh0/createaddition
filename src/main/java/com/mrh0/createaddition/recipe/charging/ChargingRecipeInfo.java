package com.mrh0.createaddition.recipe.charging;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ChargingRecipeInfo implements IRecipeTypeInfo {

    private ResourceLocation id;
    private SequencedAssemblyChargingRecipeSerializer serializer;
    private RecipeType<ChargingRecipe> type;

    public ChargingRecipeInfo(ResourceLocation id, SequencedAssemblyChargingRecipeSerializer serializer, RecipeType<ChargingRecipe> type) {
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
