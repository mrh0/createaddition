package com.mrh0.createaddition.recipe.crude_burning;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;


public class CrudeBurningRecipe implements Recipe<FluidRecipeWrapper> {

	protected final ResourceLocation id;
	protected FluidIngredient fluidIngredients;
	protected int burnTime;
	
	public static RecipeType<CrudeBurningRecipe> TYPE = new CrudeBurningRecipeType();
	@SuppressWarnings("deprecation")
	public static RecipeSerializer<?> SERIALIZER = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "crude_burning"));
	public CrudeBurningRecipe(ResourceLocation id, FluidIngredient fluid, int burnTime) {
		this.id = id;
		this.fluidIngredients = fluid;
		this.burnTime = burnTime;
	}

	@Override
	public boolean matches(FluidRecipeWrapper wrapper, Level world) {
		if(fluidIngredients == null)
			return false;
		if(wrapper == null)
			return false;
		if(wrapper.fluid == null)
			return false;
		return fluidIngredients.test(wrapper.fluid);
	}

	@Override
	public ItemStack assemble(FluidRecipeWrapper wrapper) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return new ItemStack(Items.AIR);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public FluidIngredient getFluidIngredient() {
		return fluidIngredients;
	}
	
	public int getBurnTime() {
		return this.burnTime;
	}
}
