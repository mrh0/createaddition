package com.mrh0.createaddition.blocks.electric_pump;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrh0.createaddition.index.CAPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class ElectricPumpRenderer extends KineticBlockEntityRenderer {

	public ElectricPumpRenderer(Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(KineticBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;
		if (!(be instanceof ElectricPumpBlockEntity pump))
			return;

		BlockPos pos = be.getBlockPos();
		BlockState state = be.getBlockState();
		int packedLight = LevelRenderer.getLightColor(be.getLevel(), pos);
		float renderTime = AnimationTickHolder.getRenderTime(be.getLevel());
		Direction facing = state.getValue(ElectricPumpBlock.FACING);
		Quaternionf rotation = ElectricPumpBlockEntity.getFacingRotation(facing);
		VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

		for (int i = 0; i < ElectricPumpBlockEntity.PARTIAL_OFFSETS.length; i++) {
			float[] offset = ElectricPumpBlockEntity.PARTIAL_OFFSETS[i];
			float scale = ElectricPumpBlockEntity.getPulseScale(pump.isActive(), pump.getPumpSpeed(), renderTime,
					ElectricPumpBlockEntity.PARTIAL_PHASE_OFFSETS[i]);
			CachedBuffers.partial(CAPartials.ELECTRIC_PUMP_PARTIAL, state)
					.rotateCentered(rotation)
					.translate(offset[0], offset[1], offset[2])
					.scale(scale)
					.light(packedLight)
					.renderInto(ms, vb);
		}
	}
}
