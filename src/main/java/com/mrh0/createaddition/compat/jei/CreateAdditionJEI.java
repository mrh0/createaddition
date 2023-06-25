package com.mrh0.createaddition.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.jei.category.ChargingCategory;
import com.mrh0.createaddition.compat.jei.category.RollingMillCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.compat.jei.*;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import com.simibubi.create.foundation.utility.Lang;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class CreateAdditionJEI implements IModPlugin {

	private static final ResourceLocation ID = new ResourceLocation(CreateAddition.MODID, "jei_plugin");

	@Override
	@Nonnull
	public ResourceLocation getPluginUid() {
		return ID;
	}
	
	public IIngredientManager ingredientManager;
	final List<CreateRecipeCategory<?>> ALL = new ArrayList<>();
	@SuppressWarnings("unused")
	public void loadCategories() {
		ALL.clear();

		CreateRecipeCategory<?>

		rolling = builder(RollingRecipe.class)
				.addTypedRecipes(() -> RollingRecipe.TYPE)
				.catalyst(CABlocks.ROLLING_MILL::get)
				.itemIcon(CABlocks.ROLLING_MILL.get())
				.emptyBackground(177, 53)
				.build("rolling", RollingMillCategory::new),

		charging = builder(ChargingRecipe.class)
				.addTypedRecipes(() -> ChargingRecipe.TYPE)
				.catalyst(CABlocks.TESLA_COIL::get)
				.itemIcon(CABlocks.TESLA_COIL.get())
				.emptyBackground(177, 53)
				.build("charging", ChargingCategory::new);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		loadCategories();
		registration.addRecipeCategories(ALL.toArray(IRecipeCategory[]::new));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ingredientManager = registration.getIngredientManager();

		ALL.forEach(c -> c.registerRecipes(registration));

		registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
	}

	@Override
	public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
		ALL.forEach(c -> c.registerCatalysts(registration));
	}
	
	private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
		return new CategoryBuilder<>(recipeClass);
	}

	private class CategoryBuilder<T extends Recipe<?>> {
		private final Class<? extends T> recipeClass;
		private final Predicate<CRecipes> predicate = cRecipes -> true;

		private IDrawable background;
		private IDrawable icon;

		private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
		private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

		public CategoryBuilder(Class<? extends T> recipeClass) {
			this.recipeClass = recipeClass;
		}

		public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
			recipeListConsumers.add(consumer);
			return this;
		}


		public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
			return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
		}

		public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
			catalysts.add(supplier);
			return this;
		}

		public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
			return catalystStack(() -> new ItemStack(supplier.get()
					.asItem()));
		}

		public void icon(IDrawable icon) {
			this.icon = icon;
		}

		public CategoryBuilder<T> itemIcon(ItemLike item) {
			icon(new ItemIcon(() -> new ItemStack(item)));
			return this;
		}


		public void background(IDrawable background) {
			this.background = background;
		}

		public CategoryBuilder<T> emptyBackground(int width, int height) {
			background(new EmptyBackground(width, height));
			return this;
		}

		public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
			Supplier<List<T>> recipesSupplier;
			if (predicate.test(AllConfigs.server().recipes)) {
				recipesSupplier = () -> {
					List<T> recipes = new ArrayList<>();
					for (Consumer<List<T>> consumer : recipeListConsumers)
						consumer.accept(recipes);
					return recipes;
				};
			} else {
				recipesSupplier = Collections::emptyList;
			}

			CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
					new mezz.jei.api.recipe.RecipeType<>(new ResourceLocation(CreateAddition.MODID, name), recipeClass),
					Lang.translateDirect("recipe." + name), background, icon, recipesSupplier, catalysts);
			CreateRecipeCategory<T> category = factory.create(info);
			ALL.add(category);
			return category;
		}
	}
}
