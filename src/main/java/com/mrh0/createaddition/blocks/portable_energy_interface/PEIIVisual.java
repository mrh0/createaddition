package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PSIVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;

import java.util.function.Consumer;

/**
 The same as Create's{@link PSIVisual}class but using{@link PEInstance}instead of{@link PIInstance}.
 Everything else is copied from Create's code
 */
public class PEIIVisual extends AbstractBlockEntityVisual<PortableEnergyInterfaceBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {
    private final PEInstance instance;

    public PEIIVisual(VisualizationContext visualizationContext, PortableEnergyInterfaceBlockEntity blockEntity, float partialTick) {
        super(visualizationContext, blockEntity, partialTick);

        instance = new PEInstance(visualizationContext.instancerProvider(), blockState, getVisualPosition(), isLit());
        instance.beginFrame(blockEntity.getExtensionDistance(partialTick));
    }

    @Override
    public void tick(TickableVisual.Context ctx) {
        instance.tick(isLit());
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        instance.beginFrame(blockEntity.getExtensionDistance(ctx.partialTick()));
    }

    @Override
    public void updateLight(float partialTick) {
        relight(instance.middle, instance.top);
    }

    @Override
    protected void _delete() {
        instance.remove();
    }

    private boolean isLit() {
        return blockEntity.isConnected();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        instance.collectCrumblingInstances(consumer);
    }

}