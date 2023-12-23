package com.mrh0.createaddition.blocks.modular_accumulator;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.Iterate;

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
		TransformStack msr = TransformStack.cast(ms);
		msr.translate(te.width / 2f, te.height - 0.5f, te.width / 2f);

		float dialPivotY = 6f/16f;
		float dialPivotZ = 8f/16f;
		float progress = te.gauge.getValue(partialTicks);

		for (Direction d : Iterate.horizontalDirections) {
			ms.pushPose();
			CachedBufferer.partial(CAPartials.ACCUMULATOR_GUAGE, blockState)
				.rotateY(d.toYRot())
				.unCentre()
				.translate(te.width / 2f - 6 / 16f, 0, 0)
				.light(light)
				.renderInto(ms, vb);
			CachedBufferer.partial(CAPartials.ACCUMULATOR_DIAL, blockState)
				.rotateY(d.toYRot())
				.unCentre()
				.translate(te.width / 2f - 6 / 16f, 0, 0)
				.translate(0, dialPivotY, dialPivotZ)
				.rotateX(-180 * progress)
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