package com.mrh0.createaddition.datagen.TagProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CABlockTagProvider extends BlockTagsProvider {
    public CABlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateAddition.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag).add(
                CABlocks.LIQUID_BLAZE_BURNER.get()
        );
        tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag).add(
                CABlocks.LIQUID_BLAZE_BURNER.get()
        );
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                CABlocks.ALTERNATOR.get(),
                CABlocks.ELECTRIC_MOTOR.get(),
                CABlocks.ELECTRIC_PUMP.get(),
                CABlocks.ROLLING_MILL.get(),
                CABlocks.CREATIVE_ENERGY.get(),
                CABlocks.SMALL_CONNECTOR.get(),
                CABlocks.SMALL_LIGHT_CONNECTOR.get(),
                CABlocks.LARGE_CONNECTOR.get(),
                CABlocks.REDSTONE_RELAY.get(),
                CABlocks.TESLA_COIL.get(),
                CABlocks.LIQUID_BLAZE_BURNER.get(),
                CABlocks.BARBED_WIRE.get(),
                CABlocks.MODULAR_ACCUMULATOR.get(),
                CABlocks.PORTABLE_ENERGY_INTERFACE.get(),
                CABlocks.DIGITAL_ADAPTER.get(),
                CABlocks.ELECTRUM_BLOCK.get()
        );

        tag(BlockTags.MINEABLE_WITH_HOE).add(
                CABlocks.BIOMASS_PALLET.get()
        );

        tag(BlockTags.BEACON_BASE_BLOCKS).add(CABlocks.ELECTRUM_BLOCK.get());

        tag(CATagRegister.Blocks.STORAGE_BLOCKS).add(
                CABlocks.ELECTRUM_BLOCK.get(),
                CABlocks.BIOMASS_PALLET.get()
        );

        tag(CATagRegister.Blocks.STORAGE_BLOCKS_BIOMASS).add(CABlocks.BIOMASS_PALLET.get());
        tag(CATagRegister.Blocks.STORAGE_BLOCKS_BIO).add(CABlocks.BIOMASS_PALLET.get());
        tag(CATagRegister.Blocks.STORAGE_BLOCKS_ELECTRUM).add(CABlocks.ELECTRUM_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL).add(CABlocks.ELECTRUM_BLOCK.get());
        tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).add(CABlocks.ELECTRUM_BLOCK.get());
        tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).add(CABlocks.ELECTRUM_BLOCK.get());
        tag(BlockTags.INCORRECT_FOR_STONE_TOOL).add(CABlocks.ELECTRUM_BLOCK.get());

        tag(AllTags.AllBlockTags.BRITTLE.tag).add(
                CABlocks.CHOCOLATE_CAKE.get(),
                CABlocks.HONEY_CAKE.get()
        );

        tag(CATagRegister.Blocks.FARMERSDELIGHT_MINEABLE_KNIFE).add(
                CABlocks.CHOCOLATE_CAKE.get(),
                CABlocks.HONEY_CAKE.get()
        );
    }
}
