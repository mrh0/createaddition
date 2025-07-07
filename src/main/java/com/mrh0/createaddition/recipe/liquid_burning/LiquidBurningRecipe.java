package com.mrh0.createaddition.recipe.liquid_burning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrh0.createaddition.datagen.RecipeProvider.CALiquidBurningRecipeProvider;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LiquidBurningRecipe extends StandardProcessingRecipe<FluidRecipeWrapper> implements Recipe<FluidRecipeWrapper> {
	protected FluidIngredient fluidIngredients;
	protected int burnTime;
	protected boolean superheated;
	
	public LiquidBurningRecipe(String group, FluidIngredient fluid, int burnTime, boolean superheated) {
        super(CALiquidBurningRecipeProvider.recipeType, new LiquidBurningRecipeParams());
        this.fluidIngredients = fluid;
		this.burnTime = burnTime;
		this.superheated = superheated;
	}

	@Override
	public boolean matches(@NotNull FluidRecipeWrapper wrapper, @NotNull Level world) {
		if(fluidIngredients == null) return false;
        if(wrapper.fluid == null) return false;
		return fluidIngredients.test(wrapper.fluid);
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull FluidRecipeWrapper fluidRecipeWrapper, HolderLookup.@NotNull Provider provider) {
		return new ItemStack(Items.AIR);
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 0;
	}

	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return CARecipes.LIQUID_BURNING.get();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return CARecipes.LIQUID_BURNING_TYPE.get();
	}

	public FluidIngredient getFluidIngredient() {
		return fluidIngredients;
	}
	
	public int getBurnTime() {
		return this.burnTime;
	}
	
	public boolean isSuperheated() {
		return this.superheated;
	}

	public static class Serializer implements RecipeSerializer<LiquidBurningRecipe> {
		private static final MapCodec<LiquidBurningRecipe> CODEC = RecordCodecBuilder.mapCodec(
				builder -> builder.group(
						Codec.STRING.optionalFieldOf("group", "").forGetter(LiquidBurningRecipe::getGroup),
						//CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ChargingRecipe::category),
						FluidIngredient.CODEC.fieldOf("input").forGetter(r -> r.fluidIngredients),
						Codec.INT.optionalFieldOf("burnTime", 0).forGetter(r -> r.burnTime),
						Codec.BOOL.optionalFieldOf("superheated", false).forGetter(r -> r.superheated)
				).apply(builder, LiquidBurningRecipe::new)
		);

		public static final StreamCodec<RegistryFriendlyByteBuf, LiquidBurningRecipe> STREAM_CODEC = StreamCodec.of(
				LiquidBurningRecipe.Serializer::toNetwork, LiquidBurningRecipe.Serializer::fromNetwork
		);

		@Override
		public MapCodec<LiquidBurningRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, LiquidBurningRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static LiquidBurningRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String group = buffer.readUtf();
			boolean superheated = buffer.readBoolean();
			int burnTime = buffer.readInt();
			FluidIngredient input = FluidIngredient.STREAM_CODEC.decode(buffer);
			return new LiquidBurningRecipe(group, input, burnTime, superheated);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, LiquidBurningRecipe recipe) {
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.superheated);
			buffer.writeInt(recipe.burnTime);
			FluidIngredient.STREAM_CODEC.encode(buffer, recipe.fluidIngredients);
		}
	}
}