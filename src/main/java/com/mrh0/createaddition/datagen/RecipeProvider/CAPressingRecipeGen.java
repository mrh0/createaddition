package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class CAPressingRecipeGen extends PressingRecipeGen {

    public CAPressingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateAddition.MODID);
    }

    GeneratedRecipe
    ZINC = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"zinc_ingot"), b -> b.require(CATagRegister.Items.ZINC_INGOTS)
            .output(CAItems.ZINC_SHEET)
    ),
    ELECTRUM = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID,"electrum_ingot"), b -> b.require(CATagRegister.Items.ELECTRUM_INGOTS)
            .output(CAItems.ELECTRUM_SHEET)
    )
    ;
}
