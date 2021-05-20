package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.logistics.block.inventories.CrateBlock;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class CreativeEnergyBlock extends CrateBlock implements ITE<CreativeEnergyTileEntity> {

	public static final VoxelShape CREATIVE_ENERGY_SHAPE = CAShapes.shape(1,0,1,15,16,15).add(0,2,0,16,14,16).build();
	
	public CreativeEnergyBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CREATIVE_ENERGY_SHAPE;
	}
	
	@Override
	public Class<CreativeEnergyTileEntity> getTileEntityClass() {
		return CreativeEnergyTileEntity.class;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.CREATIVE_ENERGY.create();
	}
}
