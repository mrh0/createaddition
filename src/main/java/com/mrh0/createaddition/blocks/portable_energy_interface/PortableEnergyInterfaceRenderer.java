package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.index.CABlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;


/**
 * Modified{@link PortableStorageInterfaceRenderer}but changing the PartialModels to use our models
 */
public class PortableEnergyInterfaceRenderer extends PortableStorageInterfaceRenderer {

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