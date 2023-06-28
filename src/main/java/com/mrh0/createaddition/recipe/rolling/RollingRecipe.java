package com.mrh0.createaddition.recipe.rolling;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.jei.RollingMillAssemblySubCategory;
import com.mrh0.createaddition.compat.rei.ReiRollingMillAssemblySubCategory;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

public class RollingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

    public static RecipeType<RollingRecipe> TYPE = new RollingRecipeType();
    @SuppressWarnings("deprecation")
    public static RecipeSerializer<?> SERIALIZER = Registry.RECIPE_SERIALIZER.get(new ResourceLocation(CreateAddition.MODID, "rolling"));
    protected final ItemStack output;
    protected final ResourceLocation id;
    protected final Ingredient ingredient;

    protected RollingRecipe(Ingredient ingredient, ItemStack output, ResourceLocation id) {
        super(new RollingRecipeInfo(id, (SequencedAssemblyRollingRecipeSerializer) SERIALIZER, TYPE), new RollingMillRecipeParams(id, ingredient, new ProcessingOutput(output, 1f)));
        this.output = output;
        this.id = id;
        this.ingredient = ingredient;
    }

    public static void register() {
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
        return 1;
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

	@Override
    public Component getDescriptionForAssembly() {
        return new TranslatableComponent("createaddition.recipe.rolling.sequence").withStyle(ChatFormatting.DARK_GREEN);
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> set) {
        set.add(CABlocks.ROLLING_MILL.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {

    }

    @Override
    public SequencedAssemblySubCategoryType getJEISubCategory() {
        return new SequencedAssemblySubCategoryType(() -> RollingMillAssemblySubCategory::new, () -> ReiRollingMillAssemblySubCategory::new, () -> null);
    }
}
