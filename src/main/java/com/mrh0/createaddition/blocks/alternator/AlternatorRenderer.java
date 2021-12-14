package com.mrh0.createaddition.blocks.alternator;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;


public class AlternatorRenderer extends KineticTileEntityRenderer {

	public AlternatorRenderer(Context dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
		return CachedBufferer.partial(AllBlockPartials.SHAFT_HALF, te.getBlockState());
	}
}
