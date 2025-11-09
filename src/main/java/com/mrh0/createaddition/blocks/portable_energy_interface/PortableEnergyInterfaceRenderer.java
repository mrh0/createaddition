package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Modified{@link PortableStorageInterfaceRenderer}but changing the PartialModels to use our models
 */
public class PortableEnergyInterfaceRenderer extends PortableStorageInterfaceRenderer{

    public PortableEnergyInterfaceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    static PartialModel getMiddleForState(BlockState state, boolean lit) {
        CABlocks.PORTABLE_ENERGY_INTERFACE.has(state);
        return lit ? CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE_POWERED
                : CAPartials.PORTABLE_ENERGY_INTERFACE_MIDDLE;
    }

    static PartialModel getTopForState(BlockState state) {
        return CAPartials.PORTABLE_ENERGY_INTERFACE_TOP;
    }
}
