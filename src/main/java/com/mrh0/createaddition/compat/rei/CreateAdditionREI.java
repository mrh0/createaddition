package com.mrh0.createaddition.compat.rei;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mrh0.createaddition.compat.rei.category.ChargingCategory;
import com.mrh0.createaddition.compat.rei.category.RollingMillCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.Create;
import com.simibubi.create.compat.rei.ConversionRecipe;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public class CreateAdditionREI implements REIClientPlugin {

    final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

    final CreateRecipeCategory<?> rolling = register(RollingMillCategory::new)
            .recipes(RollingRecipe.TYPE)
            .catalyst(CABlocks.ROLLING_MILL::get)
            .build();

    final CreateRecipeCategory<?> charging = register(ChargingCategory::new)
            .recipes(ChargingRecipe.TYPE)
            .catalyst(CABlocks.TESLA_COIL::get)
            .build();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (CreateRecipeCategory<?> c : ALL) {
            registry.add(c);
        }

        registry.addWorkstations(CategoryIdentifier.of(new ResourceLocation(Create.ID, "sandpaper_polishing")), EntryIngredients.of(CAItems.DIAMOND_GRIT_SANDPAPER.get()));
    }
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ALL.forEach(c -> c.registerRecipes(registry));
        List<ConversionRecipe> r1 = new ArrayList<>();
        for(ConversionRecipe recipe : r1) {
            registry.add(new CreateDisplay<>(recipe, CategoryIdentifier.of("create", "mystery_conversion")));
        }
    }

    private <T extends Recipe<?>> CategoryBuilder<T> register(Supplier<CreateRecipeCategory<T>> supplier) {
        return new CategoryBuilder<>(supplier);
    }

    private class CategoryBuilder<T extends Recipe<?>> {
        CreateRecipeCategory<T> category;
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();
        CategoryBuilder(Supplier<CreateRecipeCategory<T>> category) {
            this.category = category.get();
        }

        CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        CategoryBuilder<T> recipes(RecipeType<?> recipeTypeEntry) {
            category.recipes.add(() -> findRecipesByType(recipeTypeEntry));
            return this;
        }

        CreateRecipeCategory<T> build() {
            ALL.add(category);
            return category;
        }
    }

    static List<Recipe<?>> findRecipesByType(RecipeType<?> type) {
        return findRecipes(r -> r.getType() == type);
    }

    static List<Recipe<?>> findRecipes(Predicate<Recipe<?>> predicate) {
        return Minecraft.getInstance().level.getRecipeManager()
                .getRecipes()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
     //Build() needs a Factory to create an instance of info. some recipe fixes. 
    //String name needs to be carried along as well through the class extensions.
    //Then pray this shit fucking works
}
