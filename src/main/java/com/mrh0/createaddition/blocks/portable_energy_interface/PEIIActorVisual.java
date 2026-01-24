package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import com.simibubi.create.content.contraptions.actors.psi.PSIActorVisual;
import com.simibubi.create.content.contraptions.actors.psi.PIInstance;

/**
 * The same as Create's{@link PSIActorVisual}class but using{@link PEInstance}instead of{@link PIInstance},
 * and using{@link PortableEnergyInterfaceMovement}instead of Create's{@link PortableStorageInterfaceMovement}.
 * Everything else is copied from Create's code
 **/
public class PEIIActorVisual extends ActorVisual {
    private final PEInstance instance;
    public PEIIActorVisual(VisualizationContext context, VirtualRenderWorld world, MovementContext movementContext) {
        super(context, world, movementContext);

        PEInstance peInstance = new PEInstance(context.instancerProvider(), movementContext.state, movementContext.localPos, false);

        peInstance.middle.light(localBlockLight(), 0);
        peInstance.top.light(localBlockLight(), 0);
        instance = peInstance;
    }

    @Override
    public void beginFrame() {
        LerpedFloat lf = PortableEnergyInterfaceMovement.getAnimation(context);
        instance.tick(lf.settled());
        instance.beginFrame(lf.getValue(AnimationTickHolder.getPartialTicks()));
    }

    @Override
    protected void _delete() {
        instance.remove();
    }
}
