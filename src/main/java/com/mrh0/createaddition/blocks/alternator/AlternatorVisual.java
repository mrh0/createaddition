package com.mrh0.createaddition.blocks.alternator;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class AlternatorVisual extends KineticBlockEntityVisual<AlternatorBlockEntity> {
    protected final RotatingInstance frontShaft;
    protected final RotatingInstance backShaft;

    public AlternatorVisual(VisualizationContext context, AlternatorBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        Direction facing = blockState.getValue(BlockStateProperties.FACING);
        var instancer = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));

        frontShaft = instancer.createInstance()
                .rotateToFace(Direction.SOUTH, facing)
                .setup(blockEntity)
                .setPosition(getVisualPosition());
        frontShaft.setChanged();

        backShaft = instancer.createInstance()
                .rotateToFace(Direction.SOUTH, facing.getOpposite())
                .setup(blockEntity)
                .setPosition(getVisualPosition());
        backShaft.setChanged();
    }

    @Override
    public void update(float pt) {
        frontShaft.setup(blockEntity).setChanged();
        backShaft.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(frontShaft, backShaft);
    }

    @Override
    protected void _delete() {
        frontShaft.delete();
        backShaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(frontShaft);
        consumer.accept(backShaft);
    }
}
