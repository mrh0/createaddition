package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import com.simibubi.create.content.contraptions.actors.psi.PSIActorVisual;

import java.util.Optional;

/**
 * Modified{@link PortableStorageInterfaceMovement}but using{@link PEIIActorVisual}instead of{@link PSIActorVisual},
 * and{@link PortableEnergyInterfaceRenderer}instead of{@link PortableStorageInterfaceRenderer}
 */
public class PortableEnergyInterfaceMovement extends PortableStorageInterfaceMovement {
    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld,
                                    MovementContext movementContext) {
        return new PEIIActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization(context.world))
            PortableEnergyInterfaceRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }
}