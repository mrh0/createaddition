package com.mrh0.createaddition.blocks.rotor;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;

public class RotorVisual extends KineticBlockEntityVisual<RotorBlockEntity> implements SimpleDynamicVisual {

	protected final RotatingInstance shaft;
	protected final TransformedInstance body;
	protected final Matrix4f baseTransform = new Matrix4f();
	protected float lastAngle = Float.NaN;

	public RotorVisual(VisualizationContext context, RotorBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		Direction.Axis axis = rotationAxis();
		shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT))
				.createInstance();
		shaft.setup(blockEntity)
				.setPosition(getVisualPosition())
				.rotateToFace(axis)
				.setChanged();

		body = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(CAPartials.ROTOR))
				.createInstance();

		// The body model is authored vertically (along Y), like the shaft.
		Direction align = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
		body.translate(getVisualPosition())
				.center()
				.rotate(new Quaternionf().rotateTo(0, 1, 0, align.getStepX(), align.getStepY(), align.getStepZ()));
		baseTransform.set(body.pose);

		animate(blockEntity.angle);
	}

	@Override
	public void beginFrame(DynamicVisual.Context ctx) {
		float angle = blockEntity.getRenderAngle(ctx.partialTick());
		if (Math.abs(angle - lastAngle) < 0.001)
			return;
		animate(angle);
		lastAngle = angle;
	}

	private void animate(float angle) {
		body.setTransform(baseTransform)
				.rotateY(AngleHelper.rad(angle))
				.uncenter()
				.setChanged();
	}

	@Override
	public void update(float pt) {
		shaft.setup(blockEntity)
				.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		relight(shaft, body);
	}

	@Override
	protected void _delete() {
		shaft.delete();
		body.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(shaft);
		consumer.accept(body);
	}
}
