package com.mrh0.createaddition.blocks.modular_accumulator;

import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.mrh0.createaddition.index.CASpriteShifts;
import com.simibubi.create.api.connectivity.ConnectivityHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class ModularAccumulatorCTBehaviour extends HorizontalCTBehaviour {

	public ModularAccumulatorCTBehaviour() {
		super(CASpriteShifts.ACCUMULATOR, CASpriteShifts.ACCUMULATOR_TOP);
	}

	@Override
	public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
		BlockPos otherPos, Direction face) {
		return state.getBlock() == other.getBlock() && ConnectivityHandler.isConnected(reader, pos, otherPos);
	}
}