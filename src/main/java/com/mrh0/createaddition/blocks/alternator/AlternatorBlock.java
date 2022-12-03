package com.mrh0.createaddition.blocks.alternator;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlternatorBlock extends DirectionalKineticBlock implements ITE<AlternatorTileEntity>, IRotate {
	
	public static final VoxelShaper ALTERNATOR_SHAPE = CAShapes.shape(0, 3, 0, 16, 13, 16).add(2, 0, 2, 14, 14, 14).forDirectional();
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ALTERNATOR_SHAPE.get(state.getValue(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
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
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING)
			.getAxis();
	}
	
	@Override
	public BlockEntityType<? extends AlternatorTileEntity> getTileEntityType() {
		return CATileEntities.ALTERNATOR.get();
	}

	@Override
	public Class<AlternatorTileEntity> getTileEntityClass() {
		return AlternatorTileEntity.class;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.ALTERNATOR.create(pos, state);
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.MEDIUM;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		if(tileentity != null) {
			if(tileentity instanceof AlternatorTileEntity) {
				((AlternatorTileEntity)tileentity).updateCache();
			}
		}
	}
}
