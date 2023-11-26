package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeSerializer;
import com.mrh0.createaddition.recipe.conditions.HasFluidTagCondition;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipeSerializer;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeProcessingFactory;
import com.mrh0.createaddition.recipe.rolling.SequencedAssemblyRollingRecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CARecipes {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateAddition.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CreateAddition.MODID);

	private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
		return RECIPE_TYPES.register(id, () -> new RecipeType<>() {
			public String toString() {
				return id;
			}
		});
	}

	public static final Supplier<RecipeType<RollingRecipe>> ROLLING_TYPE = register("rolling");
	public static RegistryObject<RecipeSerializer<?>> ROLLING = SERIALIZERS.register("rolling", () ->
			new SequencedAssemblyRollingRecipeSerializer(new RollingRecipeProcessingFactory()));

	public static final Supplier<RecipeType<ChargingRecipe>> CHARGING_TYPE = register("charging");
	public static final RegistryObject<RecipeSerializer<?>> CHARGING = SERIALIZERS.register("charging", ChargingRecipeSerializer::new);

	public static final Supplier<RecipeType<LiquidBurningRecipe>> LIQUID_BURNING_TYPE = register("liquid_burning");
	public static final RegistryObject<RecipeSerializer<?>> LIQUID_BURNING = SERIALIZERS.register("liquid_burning", LiquidBurningRecipeSerializer::new);

    public static void register(IEventBus event) {

    	SERIALIZERS.register(event);
		RECIPE_TYPES.register(event);

        CraftingHelper.register(HasFluidTagCondition.Serializer.INSTANCE);
    }
}
