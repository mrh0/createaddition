package com.mrh0.createaddition.blocks.servo_motor;

import java.util.function.Consumer;

import org.joml.Quaternionf;

import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ServoMotorVisual extends KineticBlockEntityVisual<ServoMotorBlockEntity> implements SimpleDynamicVisual {

	private final OrientedInstance topInstance;
	private final Axis rotationAxis;
	private final Quaternionf blockOrientation;

	public ServoMotorVisual(VisualizationContext context, ServoMotorBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		Direction facing = blockState.getValue(BlockStateProperties.FACING);

		rotationAxis = Axis.of(Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis()).step());

		if (facing.getAxis().isHorizontal()) {
			blockOrientation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.getOpposite()));
		} else {
			blockOrientation = new Quaternionf();
		}
		blockOrientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)));

		topInstance = instancerProvider()
				.instancer(InstanceTypes.ORIENTED, Models.partial(AllPartialModels.BEARING_TOP))
				.createInstance();
		topInstance.position(getVisualPosition())
				.rotation(blockOrientation)
				.setChanged();
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		float interpolatedAngle = blockEntity.getInterpolatedAngle(ctx.partialTick() - 1);
		Quaternionf rot = rotationAxis.rotationDegrees(interpolatedAngle);
		rot.mul(blockOrientation);
		topInstance.rotation(rot).setChanged();
	}

	@Override
	public void update(float pt) {
		topInstance.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(topInstance);
	}

	@Override
	protected void _delete() {
		topInstance.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(topInstance);
	}
}
