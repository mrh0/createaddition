package com.mrh0.createaddition.recipe.rolling;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrh0.createaddition.datagen.RecipeProvider.CARollingRecipeProvider;
import com.mrh0.createaddition.index.CARecipes;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class RollingRecipe extends StandardProcessingRecipe<RecipeWrapper> implements Recipe<RecipeWrapper> {
    protected final ItemStack output;
    protected final Ingredient ingredient;

    public RollingRecipe(String group, Ingredient ingredient, ItemStack output) {
        super(CARollingRecipeProvider.recipeType, new RollingRecipeParams());
        this.output = output;
        this.ingredient = ingredient;
    }

    public static void register() {
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean matches(RecipeWrapper inv, @NotNull Level level) {
        if (inv.isEmpty()) return false;
        return ingredient.test(inv.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeWrapper recipeWrapper, HolderLookup.@NotNull Provider provider) {
        return this.output;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 0;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return this.output;
    }

    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CARecipes.ROLLING.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CARecipes.ROLLING_TYPE.get();
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return this.output;
    }

    @MethodsReturnNonnullByDefault
    public static class Serializer implements RecipeSerializer<RollingRecipe> {
        private static final MapCodec<RollingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(RollingRecipe::getGroup),
                        //CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ChargingRecipe::category),
                        Ingredient.CODEC.fieldOf("input").forGetter(r -> r.ingredient),
                        ItemStack.CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(r -> r.output)
                ).apply(builder, RollingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, RollingRecipe> STREAM_CODEC = StreamCodec.of(
                RollingRecipe.Serializer::toNetwork, RollingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<RollingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RollingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static RollingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            ItemStack output = ItemStack.STREAM_CODEC.decode(buffer);
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new RollingRecipe(group, input, output);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, RollingRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
        }
    }
}
