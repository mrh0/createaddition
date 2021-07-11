package com.mrh0.createaddition.blocks.alternator;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

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
import net.minecraft.world.World;

import com.simibubi.create.content.contraptions.base.IRotate.SpeedLevel;
import net.minecraft.block.AbstractBlock.Properties;

public class AlternatorBlock extends DirectionalKineticBlock implements ITE<AlternatorTileEntity>, IRotate {
	
	public static final VoxelShaper ALTERNATOR_SHAPE = CAShapes.shape(0, 3, 0, 16, 13, 16).add(2, 0, 2, 14, 14, 14).forDirectional();
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ALTERNATOR_SHAPE.get(state.getValue(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction preferred = getPreferredFacing(context);
		if ((context.getPlayer() != null && context.getPlayer()
			.isShiftKeyDown()) || preferred == null)
			return super.getStateForPlacement(context);
		return defaultBlockState().setValue(FACING, preferred);
	}

	public AlternatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING)
			.getAxis();
	}

	@Override
	public Class<AlternatorTileEntity> getTileEntityClass() {
		return AlternatorTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.ALTERNATOR.create();
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.MEDIUM;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		TileEntity tileentity = state.hasTileEntity() ? worldIn.getBlockEntity(pos) : null;
		if(tileentity != null) {
			if(tileentity instanceof AlternatorTileEntity) {
				((AlternatorTileEntity)tileentity).updateCache();
			}
		}
	}
}
