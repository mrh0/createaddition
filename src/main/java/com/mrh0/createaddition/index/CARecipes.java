package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.charging.ChargingRecipeSerializer;
import com.mrh0.createaddition.recipe.conditions.HasFluidTagCondition;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipeSerializer;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeProcessingFactory;
import com.mrh0.createaddition.recipe.rolling.SequencedAssemblyRollingRecipeSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CARecipes {
	public static final DeferredRegister<RecipeSerializer<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateAddition.MODID);
	
	public static RegistryObject<RecipeSerializer<?>> ROLLING = TYPES.register("rolling", () ->
		new SequencedAssemblyRollingRecipeSerializer(new RollingRecipeProcessingFactory()));

	public static RegistryObject<RecipeSerializer<?>> CHARGING = TYPES.register("charging", () ->
		new ChargingRecipeSerializer());

	public static RegistryObject<RecipeSerializer<?>> LIQUID_BURNING = TYPES.register("liquid_burning", () ->
		new LiquidBurningRecipeSerializer());
	
    public static void register(IEventBus event) {
    	
    	TYPES.register(event);

        CraftingHelper.register(HasFluidTagCondition.Serializer.INSTANCE);
    }
}