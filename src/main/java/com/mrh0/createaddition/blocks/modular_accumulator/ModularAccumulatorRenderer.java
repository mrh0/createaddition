package com.mrh0.createaddition.blocks.modular_accumulator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ModularAccumulatorRenderer extends SafeBlockEntityRenderer<ModularAccumulatorBlockEntity> {

	public ModularAccumulatorRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(ModularAccumulatorBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		if (!te.isController()) return;
		renderDial(te, partialTicks, ms, buffer, light, overlay);
		te.observe();
	}

	protected void renderDial(ModularAccumulatorBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
							  int light, int overlay) {
		BlockState blockState = te.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());
		ms.pushPose();
		TransformStack<PoseTransformStack> msr = TransformStack.of(ms);
		msr.translate(te.width / 2f, te.height - 0.5f, te.width / 2f);

		float dialPivotY = 6f/16f;
		float dialPivotZ = 8f/16f;
		float progress = te.gauge.getValue(partialTicks);

		for (Direction d : Iterate.horizontalDirections) {
			ms.pushPose();
			CachedBuffers.partial(CAPartials.ACCUMULATOR_GUAGE, blockState)
				.rotateYDegrees(d.toYRot())
				.uncenter()
				.translate(te.width / 2f - 6 / 16f, 0, 0)
				.light(light)
				.renderInto(ms, vb);
			CachedBuffers.partial(CAPartials.ACCUMULATOR_DIAL, blockState)
				.rotateYDegrees(d.toYRot())
				.uncenter()
				.translate(te.width / 2f - 6 / 16f, 0, 0)
				.translate(0, dialPivotY, dialPivotZ)
				.rotateXDegrees(-180 * progress)
				.translate(0, -dialPivotY, -dialPivotZ)
				.light(light)
				.renderInto(ms, vb);
			ms.popPose();
		}

		ms.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen(ModularAccumulatorBlockEntity te) {
		return te.isController();
	}
}