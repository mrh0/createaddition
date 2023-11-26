package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CARecipes;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ChargingRecipe implements Recipe<RecipeWrapper> {

	public final ResourceLocation id;
	public Ingredient ingredient;
	public ItemStack output;
	public int energy;
	public int maxChargeRate;

	public ChargingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack output, int energy, int maxChargeRate) {
		this.id = id;
		this.ingredient = ingredient;
		this.output = output;
		this.energy = energy;
		this.maxChargeRate = maxChargeRate;
	}


	@Override
	public boolean matches(RecipeWrapper wrapper, Level world) {
		if(ingredient == null)
			return false;
		if(wrapper == null)
			return false;
		if(wrapper.getItem(0) == null)
			return false;
		return ingredient.test(wrapper.getItem(0));
	}

	@Override
	public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
		return output;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return output;
	}

	public ItemStack getResultItem() {
		return output;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}


	@Override
	public RecipeSerializer<?> getSerializer() {
		return CARecipes.CHARGING.get();
	}


	@Override
	public RecipeType<?> getType() {
		return CARecipes.CHARGING_TYPE.get();
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxChargeRate() {
		return maxChargeRate;
	}

}
