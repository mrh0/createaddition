package com.mrh0.createaddition.recipe.charging;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.compat.emi.EmiChargingAssemblySubCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ChargingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

	public static RecipeType<ChargingRecipe> TYPE = CARecipes.CHARGING_TYPE.get();
	@SuppressWarnings("deprecation")
	public static RecipeSerializer<?> SERIALIZER = BuiltInRegistries.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "charging"));
	public final ResourceLocation id;
	public Ingredient ingredient;
	public ItemStack output;
	public int energy;
    public int maxChargeRate;

	public ChargingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack output, int energy, int maxChargeRate) {
		super(new ChargingRecipeInfo(id, (SequencedAssemblyChargingRecipeSerializer) SERIALIZER, TYPE), new ChargingRecipeParams(id, ingredient, new ProcessingOutput(output, 1f)));
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
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 1;
	}



	@Override
	public boolean canCraftInDimensions(int w, int h) {
		return true;
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
		return CARecipes.CHARGING_TYPE.get();
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxChargeRate() {
		return maxChargeRate;
	}



	@Override
	public Component getDescriptionForAssembly() {
		return Component.translatable("createaddition.recipe.charging.sequence_0")
				.append(Util.format(energy) + "fe ")
				.append(Component.translatable("createaddition.recipe.charging.sequence_1"))

				.withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> set) {
		set.add(CABlocks.TESLA_COIL.get());
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {

	}

	@Override
	public SequencedAssemblySubCategoryType getJEISubCategory() {
		return new SequencedAssemblySubCategoryType(
				() -> null,
				() -> null,
				() -> EmiChargingAssemblySubCategory::new);
	}

}