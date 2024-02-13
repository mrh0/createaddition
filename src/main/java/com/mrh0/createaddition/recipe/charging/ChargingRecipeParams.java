package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.config.Config;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class ChargingRecipeParams extends ProcessingRecipeBuilder.ProcessingRecipeParams {

    public int energy;

    public int maxChargeRate;

    protected ChargingRecipeParams(ResourceLocation id, Ingredient input, ProcessingOutput output) {
        super(id);
        if(ingredients == null) {
            ingredients =  NonNullList.create();
        }
        ingredients.add(input);
        if(results == null) {
            results = NonNullList.create();
        }
        results.add(output);
        if(fluidIngredients == null) {
            fluidIngredients = NonNullList.create();
        }
        fluidIngredients.clear();
        if(fluidResults == null) {
            fluidResults = NonNullList.create();
        }
        fluidResults.clear();
        processingDuration = Config.ROLLING_MILL_PROCESSING_DURATION.get();
        requiredHeat = HeatCondition.NONE;
        keepHeldItem = false;
    }

    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public NonNullList<ProcessingOutput> getResults() {
        return results;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public ResourceLocation getID() {
        return id;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxChargeRate() { return maxChargeRate;}
}
