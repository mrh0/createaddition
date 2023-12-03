package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeProcessingFactory;
import com.mrh0.createaddition.recipe.charging.SequencedAssemblyChargingRecipeSerializer;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipeSerializer;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeProcessingFactory;
import com.mrh0.createaddition.recipe.rolling.SequencedAssemblyRollingRecipeSerializer;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CARecipes {
	public static final LazyRegistrar<RecipeSerializer<?>> TYPES =
			LazyRegistrar.create(Registries.RECIPE_SERIALIZER, CreateAddition.MODID);
	
	static RegistryObject<RecipeSerializer<?>> ROLLING = TYPES.register("rolling", () ->
		new SequencedAssemblyRollingRecipeSerializer(new RollingRecipeProcessingFactory()));
	
	static RegistryObject<RecipeSerializer<?>> CHARGING = TYPES.register("charging", () ->
			new SequencedAssemblyChargingRecipeSerializer(new ChargingRecipeProcessingFactory()));
	
	static RegistryObject<RecipeSerializer<?>> LIQUID_BURNING = TYPES.register("liquid_burning", () ->
	new LiquidBurningRecipeSerializer());
	
    public static void register() {
    	
    	TYPES.register();
    }
}