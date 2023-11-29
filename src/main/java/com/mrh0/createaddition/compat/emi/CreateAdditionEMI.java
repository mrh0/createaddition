package com.mrh0.createaddition.compat.emi;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.emi.DoubleItemIcon;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CreateAdditionEMI implements EmiPlugin {
    //public static final Map<ResourceLocation, EmiRecipeCategory> ALL = new LinkedHashMap<>();

    public static final EmiRecipeCategory
            ROLLING = register("rolling", DoubleItemIcon.of(CABlocks.ROLLING_MILL.get(), CAItems.COPPER_SPOOL.get())),
            CHARGING = register("charging", EmiStack.of(CABlocks.TESLA_COIL.get())),
            LIQUID_BURNING = register("liquid_burning", DoubleItemIcon.of(CABlocks.LIQUID_BLAZE_BURNER.get(), CAItems.STRAW.get()))
            ;

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(ROLLING);
        registry.addCategory(CHARGING);
        registry.addCategory(LIQUID_BURNING);

        //ALL.forEach((id, category) -> registry.addCategory(category));

        registry.addWorkstation(ROLLING, EmiStack.of(CABlocks.ROLLING_MILL.get()));
        registry.addWorkstation(CHARGING, EmiStack.of(CABlocks.TESLA_COIL.get()));
        registry.addWorkstation(LIQUID_BURNING, EmiStack.of(CABlocks.LIQUID_BLAZE_BURNER.get()));

        addRollingRecipes(registry, RollingRecipe.TYPE, RollingMillCategory::new);
        addChargingRecipes(registry, ChargingRecipe.TYPE, ChargingCategory::new);
        addLiquidBurningRecipes(registry, LiquidBurningRecipe.TYPE, LiquidBurningCategory::new);
    }

    @SuppressWarnings("unchecked")
    private <T extends Recipe<?>> void addRollingRecipes(EmiRegistry registry, RecipeType<RollingRecipe> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : (List<T>) registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }

    private <T extends Recipe<?>> void addChargingRecipes(EmiRegistry registry, RecipeType<ChargingRecipe> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : (List<T>) registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }

    private <T extends Recipe<?>> void addLiquidBurningRecipes(EmiRegistry registry, RecipeType<LiquidBurningRecipe> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : (List<T>) registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }




    @SuppressWarnings("unchecked")
    private <T extends Recipe<?>> void addAll(EmiRegistry registry, AllRecipeTypes type, EmiRecipeCategory category,
                                              BiFunction<EmiRecipeCategory, T, EmiRecipe> constructor) {
        for (T recipe : (List<T>) registry.getRecipeManager().getAllRecipesFor(type.getType())) {
            registry.addRecipe(constructor.apply(category, recipe));
        }
    }

    public static boolean doInputsMatch(Recipe<?> a, Recipe<?> b) {
        if (!a.getIngredients().isEmpty() && !b.getIngredients().isEmpty()) {
            ItemStack[] matchingStacks = a.getIngredients().get(0).getItems();
            if (matchingStacks.length != 0) {
                if (b.getIngredients().get(0).test(matchingStacks[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        ResourceLocation id = CreateAddition.asResource(name);
        //ALL.put(id, category);
        return new EmiRecipeCategory(id, icon);
    }
}