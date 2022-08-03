package com.mrh0.createaddition.compat.rei;

import com.mrh0.createaddition.compat.rei.category.ChargingCategory;
import com.mrh0.createaddition.compat.rei.category.RollingMillCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.simibubi.create.Create;
import com.simibubi.create.compat.rei.*;
import com.simibubi.create.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.contraptions.fluids.VirtualFluid;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CRecipes;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.gui.container.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.RecipeManagerAccessor;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CreateAdditionREI implements REIClientPlugin {
    private static final ResourceLocation ID = Create.asResource("rei_plugin");

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private void loadCategories() {
        allCategories.clear();
        CreateRecipeCategory<?>

                charging = builder(ChargingCategory.class)
                        .addTypedRecipes((IRecipeTypeInfo) ChargingRecipe.TYPE)
                        .catalyst(CABlocks.TESLA_COIL::get)
                        .itemIcon(CABlocks.TESLA_COIL.get())
                        .emptyBackground(177, 63)
                        .build("charging", (CreateRecipeCategory.Factory<Recipe<?>>) new ChargingCategory((CreateRecipeCategory.Info<ChargingRecipe>) info)),

                rolling = builder(RollingMillCategory.class)
                        .addTypedRecipes((IRecipeTypeInfo) ChargingRecipe.TYPE)
                        .catalyst(CABlocks.ROLLING_MILL::get)
                        .itemIcon(CABlocks.ROLLING_MILL.get())
                        .emptyBackground(177, 63)
                        .build("rolling", (CreateRecipeCategory.Factory<Recipe<?>>) new RollingMillCategory(info));
    }
    EmptyBackground emptyBackground = new EmptyBackground(177, 63);
    CreateRecipeCategory.Info<?> info = new CreateRecipeCategory.Info<>(
            null,
            null,
            emptyBackground,
            null,
            null,
            null,
            177,
            63,
            null
    );

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<?> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }
    @Override
    public String getPluginProviderName() {
        return ID.toString();
    }
    @Override
    public void registerCategories(CategoryRegistry registry) {
        loadCategories();
        allCategories.forEach(category -> {
            registry.add(category);
            category.registerCatalysts(registry);
        });
    }
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        allCategories.forEach(c -> c.registerRecipes(registry));

        List<CraftingRecipe> recipes = ToolboxColoringRecipeMaker.createRecipes().toList();

        for (CraftingRecipe recipe : recipes) {
            Collection<Display> displays = registry.tryFillDisplay(recipe);
            for (Display display : displays) {
                if (Objects.equals(display.getCategoryIdentifier(), BuiltinPlugin.CRAFTING)) {
                    registry.add(display, recipe);
                }
            }
        }
    }
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractSimiContainerScreen.class, new SlotMover());
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDraggableStackVisitor(new GhostIngredientHandler<>());
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new BlueprintTransferHandler());
    }
    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntryIf(entryStack -> {
            if(entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack itemStack = entryStack.castValue();
                if(itemStack.getItem() instanceof TagDependentIngredientItem tagItem) {
                    return tagItem.shouldHide();
                }
            } else if(entryStack.getType() == VanillaEntryTypes.FLUID) {
                FluidStack fluidStack = entryStack.castValue();
                return fluidStack.getFluid() instanceof VirtualFluid;
            }
            return false;
        });
    }
    private class CategoryBuilder<T extends Recipe<?>> {
        private Predicate<CRecipes> predicate = cRecipes -> true;

        private Renderer background;
        private Renderer icon;

        private int width;
        private int height;

        private Function<T, ? extends CreateDisplay<T>> displayFactory;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<?> recipeClass) {
            Class<? extends T> recipeClass1 = (Class<? extends T>) recipeClass;
        }

        public CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
            this.predicate = predicate;
            return this;
        }

        public CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
            predicate = c -> configValue.apply(c).get();
            return this;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
            return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
        }

        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred) {
            return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add((T) recipe);
                }
            }));
        }

        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(converter.apply(recipe));
                }
            }));
        }

        public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            return addTypedRecipes(recipeTypeEntry::getType);
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateREI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType, Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> CreateREI.<T>consumeTypedRecipes(recipe -> recipes.add(converter.apply(recipe)), recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<? extends T>> recipeType, Predicate<Recipe<?>> pred) {
            return addRecipeListConsumer(recipes -> CreateREI.<T>consumeTypedRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(recipe);
                }
            }, recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipesExcluding(Supplier<RecipeType<? extends T>> recipeType,
                                                           Supplier<RecipeType<? extends T>> excluded) {
            return addRecipeListConsumer(recipes -> {
                List<Recipe<?>> excludedRecipes = getTypedRecipes(excluded.get());
                CreateREI.<T>consumeTypedRecipes(recipe -> {
                    for (Recipe<?> excludedRecipe : excludedRecipes) {
                        if (doInputsMatch(recipe, excludedRecipe)) {
                            return;
                        }
                    }
                    recipes.add(recipe);
                }, recipeType.get());
            });
        }

        public CategoryBuilder<T> removeRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> {
                List<Recipe<?>> excludedRecipes = getTypedRecipes(recipeType.get());
                recipes.removeIf(recipe -> {
                    for (Recipe<?> excludedRecipe : excludedRecipes) {
                        if (doInputsMatch(recipe, excludedRecipe)) {
                            return true;
                        }
                    }
                    return false;
                });
            });
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> icon(Renderer icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }

        public CategoryBuilder<T> background(Renderer background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            dimensions(width, height);
            return this;
        }

        public CategoryBuilder<T> width(int width) {
            this.width = width;
            return this;
        }

        public CategoryBuilder<T> height(int height) {
            this.height = height;
            return this;
        }

        public CategoryBuilder<T> dimensions(int width, int height) {
            width(width);
            height(height);
            return this;
        }

        public CategoryBuilder<T> displayFactory(Function<T, ? extends CreateDisplay<T>> factory) {
            this.displayFactory = factory;
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.SERVER.recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    if (predicate.test(AllConfigs.SERVER.recipes)) {
                        for (Consumer<List<T>> consumer : recipeListConsumers)
                            consumer.accept(recipes);
                    }
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            if (width <= 0 || height <= 0) {
                Create.LOGGER.warn("Create REI category [{}] has weird dimensions: {}x{}", name, width, height);
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    CategoryIdentifier.of(Create.asResource(name)),
                    Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts, width, height, displayFactory == null ? (recipe) -> new CreateDisplay<>(recipe, CategoryIdentifier.of(Create.asResource(name))) : displayFactory);
            CreateRecipeCategory<T> category = factory.create(info);
            allCategories.add(category);
            return category;
        }
    }

    public static void consumeAllRecipes(Consumer<Recipe<?>> consumer) {
        Minecraft.getInstance().level.getRecipeManager()
                .getRecipes()
                .forEach(consumer);
    }

    public static <T extends Recipe<?>> void consumeTypedRecipes(Consumer<T> consumer, RecipeType<?> type) {
        Map<ResourceLocation, Recipe<?>> map = ((RecipeManagerAccessor) Minecraft.getInstance()
                .getConnection()
                .getRecipeManager()).port_lib$getRecipes().get(type);
        if (map != null) {
            map.values().forEach(recipe -> consumer.accept((T) recipe));
        }
    }

    public static List<Recipe<?>> getTypedRecipes(RecipeType<?> type) {
        List<Recipe<?>> recipes = new ArrayList<>();
        consumeTypedRecipes(recipes::add, type);
        return recipes;
    }

    public static List<Recipe<?>> getTypedRecipesExcluding(RecipeType<?> type, Predicate<Recipe<?>> exclusionPred) {
        List<Recipe<?>> recipes = getTypedRecipes(type);
        recipes.removeIf(exclusionPred);
        return recipes;
    }

    public static boolean doInputsMatch(Recipe<?> recipe1, Recipe<?> recipe2) {
        if (recipe1.getIngredients()
                .isEmpty()
                || recipe2.getIngredients()
                .isEmpty()) {
            return false;
        }
        ItemStack[] matchingStacks = recipe1.getIngredients()
                .get(0)
                .getItems();
        if (matchingStacks.length == 0) {
            return false;
        }
        return recipe2.getIngredients()
                .get(0)
                .test(matchingStacks[0]);
    }
}
