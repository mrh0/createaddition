package com.mrh0.createaddition.blocks.rolling_mill;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;

public class RollingMillRenderer extends KineticBlockEntityRenderer {

	public RollingMillRenderer(Context dispatcher) {
		super(dispatcher);
	}
	
	@Override
	protected BlockState getRenderedBlockState(KineticBlockEntity te) {
		return shaft(getRotationAxisOf(te));
	}
	
	@Override
	protected void renderSafe(KineticBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
			int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		if(Backend.canUseInstancing(te.getLevel())) return;
		BlockState blockState = te.getBlockState();
		BlockPos pos = te.getBlockPos();
		
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());
		
		int packedLightmapCoords = LevelRenderer.getLightColor(te.getLevel(), pos);
		// SuperByteBuffer shaft = AllBlockPartials.SHAFT_HALF.renderOn(blockState);
		SuperByteBuffer shaft =  CachedBufferer.partial(AllPartialModels.SHAFT_HALF, blockState);
		Axis axis = getRotationAxisOf(te);
		
		shaft
			.rotateCentered(Direction.UP, axis == Axis.Z ? 0 : 90*(float)Math.PI/180f)
			.translate(0, 4f/16f, 0)
			.rotateCentered(Direction.NORTH, getAngleForTe(te, pos, axis))
			.light(packedLightmapCoords)
			.renderInto(ms, vb);
		
		shaft
			.rotateCentered(Direction.UP, axis == Axis.Z ? 180*(float)Math.PI/180f : 270*(float)Math.PI/180f)
			.translate(0, 4f/16f, 0)
			.rotateCentered(Direction.NORTH, -getAngleForTe(te, pos, axis))
			.light(packedLightmapCoords)
			.renderInto(ms, vb);
	}
}
