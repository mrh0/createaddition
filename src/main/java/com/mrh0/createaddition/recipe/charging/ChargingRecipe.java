package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChargingRecipe implements Recipe<RecipeWrapper> {

	public final ResourceLocation id;
	public Ingredient ingredient;
	public ItemStack output;
	public int energy;

	public static RecipeType<ChargingRecipe> TYPE = new ChargingRecipeType();
	public static RecipeSerializer<?> SERIALIZER = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "charging"));
	public ChargingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack output, int energy) {
		this.id = id;
		this.ingredient = ingredient;
		this.output = output;
		this.energy = energy;
	}


	@Override
	public boolean matches(@NotNull RecipeWrapper wrapper, @NotNull Level world) {
		if(ingredient == null)
			return false;
		if(wrapper.getItem(0) == null)
			return false;
		return ingredient.test(wrapper.getItem(0));
	}


	@Override
	public ItemStack assemble(@NotNull RecipeWrapper wrapper) {
		return output;
	}


	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return true;
	}


	@Override
	public ItemStack getResultItem() {
		return output;
	}


	@Override
	public ResourceLocation getId() {
		return id;
	}


	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}


	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public int getEnergy() {
		return energy;
	}

}