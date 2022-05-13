package com.mrh0.createaddition.compat.rei;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.Create;
import com.simibubi.create.compat.rei.ConversionRecipe;
import com.simibubi.create.compat.rei.CreateREI;
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
	
	final CreateRecipeCategory<?> rolling = register("rolling", RollingMillCategory::new)
		.recipes(RollingRecipe.TYPE)
		.catalyst(CABlocks.ROLLING_MILL::get)
		.build();
	
	final CreateRecipeCategory<?> crude_burning = register("crude_burning", CrudeBurningCategory::new)
			.recipes(CrudeBurningRecipe.TYPE)
			.catalyst(CABlocks.CRUDE_BURNER::get)
			.build();

	final CreateRecipeCategory<?> charging = register("charging", ChargingCategory::new)
			.recipes(ChargingRecipe.TYPE)
			.catalyst(CABlocks.TESLA_COIL::get)
			.build();

	@Override
	public void registerCategories(CategoryRegistry registry) {
		ALL.forEach(c -> {
			registry.add(c);
			c.recipeCatalysts.forEach(s -> registry.addWorkstations(c.getCategoryIdentifier(), EntryIngredients.of(s.get())));
		});

		registry.addWorkstations(CategoryIdentifier.of(new ResourceLocation(Create.ID, "sandpaper_polishing")), EntryIngredients.of(CAItems.DIAMOND_GRIT_SANDPAPER.get()));
//		registry.addWorkstations(CategoryIdentifier.of(new ResourceLocation(Create.ID, "deploying")), EntryIngredients.of(CAItems.DIAMOND_GRIT_SANDPAPER.get()));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		ALL.forEach(c -> c.recipes.forEach(s -> {
			for (Recipe<?> recipe : s.get()) {
				registry.add(new CreateDisplay<>(recipe, c.getCategoryIdentifier()), recipe);
			}
		}));

		List<ConversionRecipe> r1 = new ArrayList<>();
		//r1.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), CAItems.OVERCHARGED_ALLOY.asStack()));

		for(ConversionRecipe recipe : r1) {
			registry.add(new CreateDisplay<>(recipe, CategoryIdentifier.of("create", "mystery_conversion")));
		}
	}
	
	private <T extends Recipe<?>> CategoryBuilder<T> register(String name, Supplier<CreateRecipeCategory<T>> supplier) {
		return new CategoryBuilder<T>(name, supplier);
	}
	
	private class CategoryBuilder<T extends Recipe<?>> {
		CreateRecipeCategory<T> category;

		CategoryBuilder(String name, Supplier<CreateRecipeCategory<T>> category) {
			this.category = category.get();
			this.category.setCategoryId(name);
		}

		CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
			return catalystStack(() -> new ItemStack(supplier.get()
				.asItem()));
		}

		CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
			category.recipeCatalysts.add(supplier);
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
	
	@SuppressWarnings("resource")
	static List<Recipe<?>> findRecipes(Predicate<Recipe<?>> predicate) {
		return Minecraft.getInstance().level.getRecipeManager()
			.getRecipes()
			.stream()
			.filter(predicate)
			.collect(Collectors.toList());
	}
}
