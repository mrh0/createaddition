package com.mrh0.createaddition.datagen.TagProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CAFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CAFluidTagProvider extends FluidTagsProvider {
    public CAFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, CreateAddition.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CATagRegister.Fluids.BIOFUEL).add(
                CAFluids.BIOETHANOL.get(),
                CAFluids.BIOETHANOL.getSource()
        );
        tag(CATagRegister.Fluids.PLANTOIL).add(
                CAFluids.SEED_OIL.get(),
                CAFluids.SEED_OIL.getSource()
        );

        var ignites = tag(CATagRegister.Fluids.IGNITES);
        List.of(
                CATagRegister.Fluids.BIOFUEL,
                CATagRegister.Fluids.CREOSOTE,
                CATagRegister.Fluids.CRUDE_OIL,
                CATagRegister.Fluids.PLANTOIL,
                CATagRegister.Fluids.ETHANOL,
                CATagRegister.Fluids.GASOLINE,
                CATagRegister.Fluids.DIESEL,
                CATagRegister.Fluids.BIODIESEL
        ).forEach(ignites::addOptionalTag);

        /*
        tag(FluidTags.WATER).add(
                CAFluids.SEED_OIL.get(),
                CAFluids.SEED_OIL.getSource(),
                CAFluids.BIOETHANOL.get(),
                CAFluids.BIOETHANOL.getSource());
         */

        tag(CATagRegister.Fluids.CREOSOTE);
        tag(CATagRegister.Fluids.CRUDE_OIL);

    }
}
