package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.RecipeGen.LiquidBurningRecipeGen;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.recipe.conditions.HasFluidTagCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public class CALiquidBurningRecipeProvider extends LiquidBurningRecipeGen {
    public CALiquidBurningRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateAddition.MODID);
    }

    GeneratedRecipe conditional(String name, TagKey<Fluid> fluid, int burnTime) {
        return create(name, (b) -> b.fluid(fluid)
                .burnTime(burnTime)
                .withCondition(new HasFluidTagCondition(fluid))
        );
    }

    GeneratedRecipe
            BIOFUEL = create("biofuel", (b) -> b.fluid(CATagRegister.Fluids.BIOFUEL).burnTime(24000).superheated()),
            PLANTOIL = create("plantoil", (b) -> b.fluid(CATagRegister.Fluids.PLANTOIL).burnTime(4800)),
            LAVA = create("lava", (b) -> b.fluid(FluidTags.LAVA).burnTime(20000)),
            BIODIESEL = conditional("biodiesel", CATagRegister.Fluids.BIODIESEL, 24000),
            CREOSOTE = conditional("creosote", CATagRegister.Fluids.CREOSOTE, 4800),
            CRUDE_OIL = conditional("crude_oil", CATagRegister.Fluids.CRUDE_OIL, 9600),
            DIESEL = conditional("diesel", CATagRegister.Fluids.DIESEL, 24000),
            ETHANOL = conditional("ethanol", CATagRegister.Fluids.ETHANOL, 8000),
            GASOLINE = conditional("gasoline", CATagRegister.Fluids.GASOLINE, 24000);
}
