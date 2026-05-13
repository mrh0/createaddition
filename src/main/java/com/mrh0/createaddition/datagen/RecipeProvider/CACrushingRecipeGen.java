package com.mrh0.createaddition.datagen.RecipeProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class CACrushingRecipeGen extends CrushingRecipeGen {
    public CACrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, CreateAddition.MODID);
    }

    protected GeneratedRecipe mineralRecyclingCCA(String name, AllPaletteStoneTypes type, UnaryOperator<StandardProcessingRecipe.Builder<CrushingRecipe>> transform) {
        create(ResourceLocation.fromNamespaceAndPath("create", name + "_recycling"), b -> transform.apply(b.require(type.materialTag)));
        return create(type.getBaseBlock()::get, transform);
    }

    GeneratedRecipe

    DIAMOND = create(CreateAddition.MODID, () -> Items.DIAMOND, b -> b.duration(300)
            .output(CAItems.DIAMOND_GRIT).withCondition(new NotCondition(new TagEmptyCondition(CATagRegister.Items.DIAMOND_DUSTS)))
    ),

    OCHRUM = mineralRecyclingCCA("ochrum", AllPaletteStoneTypes.OCHRUM, b -> b.duration(250)
        .output(0.2f, AllItems.CRUSHED_GOLD)
        .output(0.2f, Items.GOLD_NUGGET)
        .output(0.2f, CAItems.ELECTRUM_NUGGET)
    ),
    TUFF = mineralRecyclingCCA("tuff", AllPaletteStoneTypes.TUFF, b -> b.duration(350)
        .output(0.25f, Items.FLINT)
        .output(0.1f, Items.GOLD_NUGGET)
        .output(0.1f, AllItems.COPPER_NUGGET)
        .output(0.1f, AllItems.ZINC_NUGGET)
        .output(0.1f, Items.IRON_NUGGET)
        .output(0.1f, CAItems.ELECTRUM_NUGGET)
    );
}
