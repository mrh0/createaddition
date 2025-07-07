package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CAFillingRecipeGen extends FillingRecipeGen {
    public CAFillingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries, CreateAddition.MODID);
    }

    GeneratedRecipe
    CAKE = create(ResourceLocation.fromNamespaceAndPath(CreateAddition.MODID, "cake"), b -> b.require(CAItems.CAKE_BASE_BAKED)
            .require(Tags.Fluids.MILK,1000)
            .output(Items.CAKE)
    ),
    CHOCOLATE_CAKE = create(CABlocks.CHOCOLATE_CAKE.getId(), b -> b.require(CAItems.CAKE_BASE_BAKED)
            .require(AllFluids.CHOCOLATE.getSource(), 500)
            .output(CABlocks.CHOCOLATE_CAKE.asItem())
    ),
    HONEY_CAKE = create(CABlocks.HONEY_CAKE.getId(), b -> b.require(CAItems.CAKE_BASE_BAKED)
            .require(Tags.Fluids.HONEY, 500)
            .output(CABlocks.HONEY_CAKE.asItem())
    )
    ;
}
