package com.mrh0.createaddition.blocks.rotor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class RotorRenderer extends KineticBlockEntityRenderer<RotorBlockEntity> {

	public RotorRenderer(Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(RotorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		// Renders the shaft (see getRenderedBlockState); skips itself when visualization is supported.
		super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		Direction.Axis axis = getRotationAxisOf(be);
		Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
		SuperByteBuffer body = CachedBuffers.partialFacingVertical(CAPartials.ROTOR, be.getBlockState(), facing);
		kineticRotationTransform(body, be, axis, AngleHelper.rad(be.getRenderAngle(partialTicks)), light);
		body.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	protected BlockState getRenderedBlockState(RotorBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	}
}
