package com.mrh0.createaddition.blocks.furnace_burner;


import java.util.List;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBurnerTileEntity extends AbstractBurnerBlockEntity {

	public FurnaceBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
	}
}
