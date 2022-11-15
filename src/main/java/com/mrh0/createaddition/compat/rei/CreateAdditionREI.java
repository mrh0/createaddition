package com.mrh0.createaddition.compat.rei;

import com.mrh0.createaddition.compat.rei.category.ChargingCategory;
import com.mrh0.createaddition.compat.rei.category.RollingMillCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.Create;
import com.simibubi.create.compat.rei.CreateREI;
import com.simibubi.create.compat.rei.EmptyBackground;
import com.simibubi.create.compat.rei.ItemIcon;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.utility.Lang;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CreateAdditionREI implements REIClientPlugin {

    final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();

    @SuppressWarnings("unused")
    private void loadCategories() {
        ALL.clear();

        CreateRecipeCategory<?>

        rolling = builder(RollingRecipe.class)
                .addTypedRecipes(RollingRecipe.TYPE)
                .catalyst(CABlocks.ROLLING_MILL::get)
                .itemIcon(CABlocks.ROLLING_MILL.get())
                .emptyBackground(178, 63)
                .build("rolling", RollingMillCategory::new),

        charging = builder(ChargingRecipe.class)
                .addTypedRecipes(ChargingRecipe.TYPE)
                .catalyst(CABlocks.TESLA_COIL::get)
                .itemIcon(CABlocks.TESLA_COIL.get())
                .emptyBackground(178, 63)
                .build("charging", ChargingCategory::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        loadCategories();
        ALL.forEach(category -> {
            registry.add(category);
            category.registerCatalysts(registry);
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ALL.forEach(c -> c.registerRecipes(registry));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> {
        Class<? extends T> recipeClass;
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();
        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();

        private Renderer background;
        private Renderer icon;

        private int width;
        private int height;
        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        @SuppressWarnings("unused")
        private Function<T, ? extends CreateDisplay<T>> displayFactory;


        CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }


        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipes(RecipeType<? extends T> recipeType) {
            return addRecipeListConsumer(recipes -> CreateREI.<T>consumeTypedRecipes(recipes::add, recipeType));
        }


        public void background(Renderer background) {
            this.background = background;
        }
        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            dimensions(width, height);
            return this;
        }
        public void width(int width) {
            this.width = width;
        }

        public void height(int height) {
            this.height = height;
        }

        public void dimensions(int width, int height) {
            width(width);
            height(height);
        }
        public void icon(Renderer icon) {
            this.icon = icon;
        }
        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }
        CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            recipesSupplier = () -> {
                List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                return recipes;
                };
            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    CategoryIdentifier.of(Create.asResource(name)),
                    Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts, width, height, displayFactory == null ? (recipe) -> new CreateDisplay<>(recipe, CategoryIdentifier.of(Create.asResource(name))) : displayFactory);
            CreateRecipeCategory<T> category = factory.create(info);
            ALL.add(category);
            return category;
        }
    }

    //Build() needs a Factory to create an instance of info. some recipe fixes.
    //String name needs to be carried along as well through the class extensions.
    //Then pray this shit fucking works
}
