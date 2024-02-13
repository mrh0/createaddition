package com.mrh0.createaddition.recipe.charging;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ChargingRecipeProcessingFactory implements ProcessingRecipeBuilder.ProcessingRecipeFactory<ChargingRecipe> {
    @Override
    public ChargingRecipe create(ProcessingRecipeBuilder.ProcessingRecipeParams processingRecipeParams) {
        var params = (ChargingRecipeParams) processingRecipeParams;
        Ingredient ingredient = Ingredient.EMPTY;
        ItemStack result = ItemStack.EMPTY;
        int energy = 0;
        int maxChargeRate = 0;
        if (!params.getIngredients().isEmpty()) {
            ingredient = params.getIngredients().get(0);
        }
        if (!params.getResults().isEmpty()) {
            result = params.getResults().get(0).getStack();
        }
        if (params.getEnergy() > 0) {
            energy = params.getEnergy();
        }
        if (params.getMaxChargeRate() > 0) {
            maxChargeRate = params.getMaxChargeRate();
        }
        return new ChargingRecipe(params.getID(), ingredient, result, energy, maxChargeRate);
    }
}
