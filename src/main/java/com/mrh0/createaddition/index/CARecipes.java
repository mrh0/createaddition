package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeSerializer;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipeSerializer;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeProcessingFactory;
import com.mrh0.createaddition.recipe.rolling.SequencedAssemblyRollingRecipeSerializer;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class CARecipes {
	public static final LazyRegistrar<RecipeSerializer<?>> SERIALIZERS =
			LazyRegistrar.create(Registry.RECIPE_SERIALIZER, CreateAddition.MODID);

	public static final LazyRegistrar<RecipeType<?>> RECIPE_TYPES = LazyRegistrar.create(Registry.RECIPE_TYPE, CreateAddition.MODID);
	private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
		return RECIPE_TYPES.register(id, () -> new RecipeType<>() {
			public String toString() {
				return id;
			}
		});
	}

	public static final Supplier<RecipeType<RollingRecipe>> ROLLING_TYPE = register("rolling");
	static RegistryObject<RecipeSerializer<?>> ROLLING = SERIALIZERS.register("rolling", () ->
			new SequencedAssemblyRollingRecipeSerializer(new RollingRecipeProcessingFactory()));

	public static final Supplier<RecipeType<ChargingRecipe>> CHARGING_TYPE = register("charging");
	static RegistryObject<RecipeSerializer<?>> CHARGING = SERIALIZERS.register("charging", () ->
			new ChargingRecipeSerializer());

	public static final Supplier<RecipeType<LiquidBurningRecipe>> LIQUID_BURNING_TYPE = register("liquid_burning");
	static RegistryObject<RecipeSerializer<?>> LIQUID_BURNING = SERIALIZERS.register("liquid_burning", () ->
			new LiquidBurningRecipeSerializer());
	
    public static void register() {
    	RECIPE_TYPES.register();
    	SERIALIZERS.register();
    }
}