package com.mrh0.createaddition.blocks.treated_gearbox;

import com.mrh0.createaddition.index.CAIETileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import net.minecraft.block.AbstractBlock.Properties;

public class TreatedGearboxBlock extends HorizontalKineticBlock implements ITE<TreatedGearboxTileEntity> {
	
	public static final VoxelShaper TREATED_GEARBOX_SHAPE = CAShapes.shape(0, 0, 0, 16, 10, 16).add(1, 10, 1, 15, 15, 15).forDirectional();

	public TreatedGearboxBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return TREATED_GEARBOX_SHAPE.get(state.getValue(HORIZONTAL_FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState()
				.setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CAIETileEntities.TREATED_GEARBOX.create();
	}
	
	@Override
	public Class<TreatedGearboxTileEntity> getTileEntityClass() {
		return TreatedGearboxTileEntity.class;
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}
}
