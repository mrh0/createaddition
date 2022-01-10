package com.mrh0.createaddition.blocks.furnace_burner;


import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;

import net.minecraft.core.BlockPos;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBurnerTileEntity extends AbstractBurnerBlockEntity {

	public FurnaceBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
