package com.mrh0.createaddition.recipe.rolling;

import com.mrh0.createaddition.CreateAddition;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.contraptions.itemAssembly.IAssemblyRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class RollingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

	protected final ItemStack output;
	protected final ResourceLocation id;
	protected final Ingredient ingredient;
	
	public static RecipeType<RollingRecipe> TYPE = new RollingRecipeType();
	@SuppressWarnings("deprecation")
	public static RecipeSerializer<?> SERIALIZER = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "rolling"));

	protected RollingRecipe(Ingredient ingredient, ItemStack output, ResourceLocation id) {
		super(new RollingRecipeInfo(id,SERIALIZER,TYPE),new RollingMillRecipeParams(id,ingredient,new ProcessingOutput(output,1f)));
		this.output = output;
		this.id = id;
		this.ingredient = ingredient;
	}
	
	public Ingredient getIngredient() {
		return ingredient;
	}
	
	@Override
	public boolean matches(RecipeWrapper inv, Level worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredient.test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 100;
	}

	@Override
	public ItemStack assemble(RecipeWrapper inv) {
		return this.output;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height > 0;
	}

	@Override
	public ItemStack getResultItem() {
		return this.output;
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
	
	@Override
	public ItemStack getToastSymbol() {
		return this.output;
	}
	
	@Override
	public boolean isSpecial() {
		return true;
	}
	
	public static void register() {};

	@Override
	public Component getDescriptionForAssembly() {
		return new TextComponent("Rolling in Mill").withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> set) {
		set.add(CABlocks.ROLLING_MILL.get());
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {

	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> RollingSubCategory::new;
	}
}
