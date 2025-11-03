package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.actors.psi.PSIActorVisual;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

import org.jetbrains.annotations.Nullable;

/**
 * Modified{@link PortableStorageInterfaceMovement}but using{@link PEIIActorVisual}instead of{@link PSIActorVisual},
 * and{@link PortableEnergyInterfaceRenderer}instead of{@link PortableStorageInterfaceRenderer}
 */
public class PortableEnergyInterfaceMovement extends PortableStorageInterfaceMovement{
    @Nullable
    @Override
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld,
                                    MovementContext movementContext) {
        return new PEIIActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization(context.world))
            PortableEnergyInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }
}
