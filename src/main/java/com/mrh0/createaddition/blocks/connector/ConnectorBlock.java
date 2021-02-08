package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ConnectorBlock extends Block implements ITE<ConnectorTileEntity> {

	public ConnectorBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.CONNECTOR.create();
	}

	@Override
	public Class<ConnectorTileEntity> getTileEntityClass() {
		return ConnectorTileEntity.class;
	}
}
