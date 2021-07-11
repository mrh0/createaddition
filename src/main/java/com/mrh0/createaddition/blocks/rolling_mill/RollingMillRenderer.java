package com.mrh0.createaddition.blocks.rolling_mill;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;

public class RollingMillRenderer  extends KineticTileEntityRenderer {

	public RollingMillRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return shaft(getRotationAxisOf(te));
	}
	
	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer,
			int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		BlockState blockState = te.getBlockState();
		BlockPos pos = te.getBlockPos();
		
		IVertexBuilder vb = buffer.getBuffer(RenderType.solid());
		
		int packedLightmapCoords = WorldRenderer.getLightColor(te.getLevel(), pos);
		// SuperByteBuffer shaft = AllBlockPartials.SHAFT_HALF.renderOn(blockState);
		SuperByteBuffer shaft =  PartialBufferer.get(AllBlockPartials.SHAFT_HALF, blockState);
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
