package com.mrh0.createaddition.blocks.chunkloader;

import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ChunkLoaderBlock extends Block implements ITE<ChunkLoaderTileEntity> {
	
	public ChunkLoaderBlock(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return super.createTileEntity(state, world);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return super.hasTileEntity(state);
	}

	@Override
	public Class<ChunkLoaderTileEntity> getTileEntityClass() {
		return ChunkLoaderTileEntity.class;
	}
}
