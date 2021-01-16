package com.mrh0.createaddition.blocks.electric_motor;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
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

public class ElectricMotorBlock extends DirectionalKineticBlock implements ITE<ElectricMotorTileEntity> {
	
	private static final VoxelShape SHAPE_X = Block.makeCuboidShape(2, 0, 0, 14, 16, 16);
	private static final VoxelShape SHAPE_Y = Block.makeCuboidShape(0, 2, 0, 16, 14, 16);
	private static final VoxelShape SHAPE_Z = Block.makeCuboidShape(0, 0, 2, 16, 16, 14);
	

	public ElectricMotorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.get(FACING).getAxis()) {
			case X:
				return SHAPE_X;
			case Y:
				return SHAPE_Y;
			case Z:
				return SHAPE_Z;
		}
		return SHAPE_X;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction preferred = getPreferredFacing(context);
		if ((context.getPlayer() != null && context.getPlayer()
			.isSneaking()) || preferred == null)
			return super.getStateForPlacement(context);
		return getDefaultState().with(FACING, preferred);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.ELECTRIC_MOTOR.create();
	}
	
	@Override
	public Class<ElectricMotorTileEntity> getTileEntityClass() {
		return ElectricMotorTileEntity.class;
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING)
			.getAxis();
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}
}
