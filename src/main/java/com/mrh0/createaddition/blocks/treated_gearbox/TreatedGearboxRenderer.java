package com.mrh0.createaddition.blocks.treated_gearbox;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class TreatedGearboxRenderer extends KineticTileEntityRenderer {

	public TreatedGearboxRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
		return PartialBufferer.getFacing(AllBlockPartials.SHAFT_HALF, te.getBlockState());
	}
}

