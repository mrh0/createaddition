package com.mrh0.createaddition.blocks.rolling_mill;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;

import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RollingMillVisual extends KineticBlockEntityVisual<RollingMillBlockEntity> {
    protected RotatingInstance rotatingModel1;
    protected RotatingInstance rotatingModel2;

    public RollingMillVisual(VisualizationContext context, RollingMillBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        final Direction direction = blockState.getValue(RollingMillBlock.HORIZONTAL_FACING);
        final Direction.Axis axis = direction.getAxis();

        var instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT));

        this.rotatingModel1 = instancer.createInstance().rotateToFace(Direction.UP, axis);
        this.rotatingModel2 = instancer.createInstance().rotateToFace(Direction.UP, axis);

        rotatingModel1.setup(blockEntity, axis)
                .setPosition(getVisualPosition())
                .setChanged();

        rotatingModel2.setup(blockEntity, axis)
                .setPosition(getVisualPosition())
                .nudge(0, 4f/16f, 0)
                .setRotationalSpeed(-blockEntity.getSpeed()*partialTick*8f)
                .setChanged();

    }

    @Override
    public void update(float v) {
        final Direction direction = blockState.getValue(RollingMillBlock.HORIZONTAL_FACING);
        final Direction.Axis axis = direction.getAxis();
        rotatingModel1.setup(blockEntity, axis, blockEntity.getSpeed()).setChanged();
        rotatingModel2.setup(blockEntity, axis, -blockEntity.getSpeed()).setChanged();
    }

    public void updateLight(float v) {
        this.relight(this.pos, this.rotatingModel1, this.rotatingModel2);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(rotatingModel1);
        consumer.accept(rotatingModel2);
    }

    @Override
    protected void _delete() {
        rotatingModel1.delete();
        rotatingModel2.delete();
    }
}