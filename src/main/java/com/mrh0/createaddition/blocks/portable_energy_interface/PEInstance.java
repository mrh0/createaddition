package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceRenderer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * The same as Create's{@link PIInstance}class but using{@link PortableEnergyInterfaceRenderer}instead of{@link PortableStorageInterfaceRenderer}
 * for the partial models. Everything else is copied from Create's code
 */
public class PEInstance {

    private final InstancerProvider instancerProvider;
    private final BlockState blockState;
    private final BlockPos instancePos;
    private final float angleX;
    private final float angleY;

    private boolean lit;
    TransformedInstance middle;
    TransformedInstance top;

    public PEInstance(InstancerProvider instancerProvider, BlockState blockState, BlockPos instancePos, boolean lit) {
        this.instancerProvider = instancerProvider;
        this.blockState = blockState;
        this.instancePos = instancePos;
        Direction facing = blockState.getValue(PortableStorageInterfaceBlock.FACING);
        angleX = facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
        angleY = AngleHelper.horizontalAngle(facing);
        this.lit = lit;

        middle = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(PortableEnergyInterfaceRenderer.getMiddleForState(blockState, lit)))
                .createInstance();
        top = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(PortableEnergyInterfaceRenderer.getTopForState(blockState)))
                .createInstance();
    }

    public void beginFrame(float progress) {
        middle.setIdentityTransform()
                .translate(instancePos)
                .center()
                .rotateYDegrees(angleY)
                .rotateXDegrees(angleX)
                .uncenter();

        top.setIdentityTransform()
                .translate(instancePos)
                .center()
                .rotateYDegrees(angleY)
                .rotateXDegrees(angleX)
                .uncenter();

        middle.translate(0, progress * 0.5f + 0.375f, 0);
        top.translate(0, progress, 0);

        middle.setChanged();
        top.setChanged();
    }

    public void tick(boolean lit) {
        if (this.lit != lit) {
            this.lit = lit;
            instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(PortableEnergyInterfaceRenderer.getMiddleForState(blockState, lit)))
                    .stealInstance(middle);
        }
    }

    public void remove() {
        middle.delete();
        top.delete();
    }

    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(middle);
        consumer.accept(top);
    }

}