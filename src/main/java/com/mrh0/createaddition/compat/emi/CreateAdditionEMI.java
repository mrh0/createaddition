package com.mrh0.createaddition.compat.emi;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.emi.category.ChargingCategory;
import com.mrh0.createaddition.compat.emi.category.RollingMillCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;


public class CreateAdditionEMI implements EmiPlugin {
    public static final Map<ResourceLocation, EmiRecipeCategory> ALL = new LinkedHashMap<>();

    public static final EmiRecipeCategory
            Charging = register("charging", EmiStack.of(CABlocks.TESLA_COIL.get())),
            RollingMill = register("rolling", EmiStack.of(CABlocks.ROLLING_MILL.get()));

    @Override
    public void register(EmiRegistry registry) {
        var recipes = registry.getRecipeManager();
        ALL.forEach((id, category) -> registry.addCategory(category));

        registry.addWorkstation(Charging, EmiStack.of(CABlocks.TESLA_COIL.get()));
        registry.addWorkstation(RollingMill, EmiStack.of(CABlocks.ROLLING_MILL.get()));

        recipes.getAllRecipesFor(ChargingRecipe.TYPE).stream()
                .parallel().map(ChargingCategory::new)
                .forEach(registry::addRecipe);

        recipes.getAllRecipesFor(RollingRecipe.TYPE).stream()
                .parallel().map(RollingMillCategory::new)
                .forEach(registry::addRecipe);

    }

    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        ResourceLocation id = new ResourceLocation(CreateAddition.MODID, name);
        EmiRecipeCategory category = new EmiRecipeCategory(id, icon);
        ALL.put(id, category);
        return category;
    }
}
