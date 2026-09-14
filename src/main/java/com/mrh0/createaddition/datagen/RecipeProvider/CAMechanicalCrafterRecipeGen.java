package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class CAMechanicalCrafterRecipeGen extends MechanicalCraftingRecipeGen {
    public CAMechanicalCrafterRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateAddition.MODID);
    }

    GeneratedRecipe
    ALTERNATOR = create(CABlocks.ALTERNATOR::get).recipe(b -> b
            //.key('C', CAItems.CAPACITOR)
            .key('I', CATagRegister.Items.commonTags("plates","iron"))
            .key('R', CATagRegister.Items.IRON_RODS)
            .key('S', CAItems.COPPER_SPOOL)
            .key('A', AllItems.ANDESITE_ALLOY)
            .patternLine("  A  ")
            .patternLine(" ISI ")
            .patternLine("ISRSI")
            .patternLine(" ISI ")
            .patternLine("  A  ")
    ),
    ELECTRIC_MOTOR = create(CABlocks.ELECTRIC_MOTOR::get).recipe(b -> b
            .key('A', AllItems.ANDESITE_ALLOY)
            .key('B', CATagRegister.Items.commonTags("plates","brass"))
            .key('C', CAItems.CAPACITOR)
            .key('R', CATagRegister.Items.IRON_RODS)
            .key('S', CAItems.COPPER_SPOOL)
            .patternLine("  A  ")
            .patternLine(" BSB ")
            .patternLine("BSRSB")
            .patternLine(" BCB ")
    ),
    TESLA_COIL = create(CABlocks.TESLA_COIL::get).recipe(b -> b
            .key('A', AllItems.ANDESITE_ALLOY)
            .key('B', AllBlocks.BRASS_CASING.asItem())
            .key('C', CAItems.CAPACITOR)
            .key('P', CATagRegister.Items.commonTags("plates","brass"))
            .key('S', CAItems.COPPER_SPOOL)
            .key('E', AllItems.ELECTRON_TUBE)
            .patternLine("SSS")
            .patternLine(" A ")
            .patternLine("CBC")
            .patternLine("PEP")
    ),
    ELECTRIC_PUMP = create(CABlocks.ELECTRIC_PUMP::get).recipe(b -> b
            .key('K', Items.DRIED_KELP)
            .key('P', AllBlocks.MECHANICAL_PUMP.asItem())
            .key('S', CAItems.COPPER_SPOOL)
            .key('C', CAItems.CAPACITOR)
            .patternLine(" K ")
            .patternLine("SPS")
            .patternLine(" K ")
            .patternLine(" C ")
    );
}
