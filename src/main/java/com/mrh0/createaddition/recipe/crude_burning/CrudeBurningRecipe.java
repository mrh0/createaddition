package com.mrh0.createaddition.recipe.crude_burning;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class CrudeBurningRecipe implements IRecipe<FluidRecipeWrapper> {

	protected final ResourceLocation id;
	protected FluidIngredient fluidIngredients;
	protected int burnTime;
	
	public static IRecipeType<CrudeBurningRecipe> TYPE = new CrudeBurningRecipeType();
	@SuppressWarnings("deprecation")
	public static IRecipeSerializer<?> SERIALIZER = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "crude_burning"));
	public CrudeBurningRecipe(ResourceLocation id, FluidIngredient fluid, int burnTime) {
		this.id = id;
		this.fluidIngredients = fluid;
		this.burnTime = burnTime;
	}

	@Override
	public boolean matches(FluidRecipeWrapper wrapper, World world) {
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
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}

	public FluidIngredient getFluidIngredient() {
		return fluidIngredients;
	}
	
	public int getBurnTime() {
		return this.burnTime;
	}
}
