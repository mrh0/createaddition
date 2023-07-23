package com.mrh0.createaddition.blocks.electric_motor;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;


public class ElectricMotorRenderer extends KineticBlockEntityRenderer {

	public ElectricMotorRenderer(Context dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticBlockEntity te, BlockState state) {
		return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state);
	}
}

