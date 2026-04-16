package com.mrh0.createaddition.fire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface IFlammableFluid {
    BlockState onBurned(LevelAccessor level, BlockState fireState, BlockPos fluidPos, FluidState original);
}
