package com.mrh0.createaddition.datagen.RecipeBuilders;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Objects;

public class CALiquidBurningRecipeBuilder extends CARecipeBuilder {
    protected SizedFluidIngredient ingredient;
    protected int burnTime;
    protected boolean superheated;

    public CALiquidBurningRecipeBuilder(int burnTime) {
        super(ItemStack.EMPTY);
        this.ingredient = SizedFluidIngredient.of(FluidStack.EMPTY);
        this.burnTime = burnTime;
        this.superheated = false;
    }

    public static CALiquidBurningRecipeBuilder liquidBurning(int burnTime) {
        return new CALiquidBurningRecipeBuilder(burnTime);
    }

    public CALiquidBurningRecipeBuilder require(SizedFluidIngredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public CALiquidBurningRecipeBuilder require(Fluid fluid) {
        this.ingredient = SizedFluidIngredient.of(fluid, 1000);
        return this;
    }

    public CALiquidBurningRecipeBuilder require(TagKey<Fluid> fluidTag) {
        this.ingredient = SizedFluidIngredient.of(fluidTag, 1000);
        return this;
    }

    public CALiquidBurningRecipeBuilder superheated() {
        this.superheated = true;
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
//        LiquidBurningRecipe chargingRecipe = new LiquidBurningRecipe(
//                Objects.requireNonNullElse(this.group, ""),
//                this.ingredient,
//                this.burnTime,
//                this.superheated
//        );
//        recipeOutput.accept(resourceLocation.withPrefix("liquid_burning/"), chargingRecipe, null);
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        save(recipeOutput, ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, id));
    }
}
