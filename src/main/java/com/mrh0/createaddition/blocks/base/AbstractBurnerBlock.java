package com.mrh0.createaddition.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class AbstractBurnerBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = AbstractFurnaceBlock.LIT;

	protected AbstractBurnerBlock(BlockBehaviour.Properties p_48687_) {
		super(p_48687_);
		this.registerDefaultState(
				this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.valueOf(false)));
	}

	public BlockState getStateForPlacement(BlockPlaceContext p_48689_) {
		return this.defaultBlockState().setValue(FACING, p_48689_.getHorizontalDirection().getOpposite());
	}

	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState p_48716_,
			boolean p_48717_) {
		if (!state.is(p_48716_.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof AbstractBurnerBlockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, pos, (AbstractBurnerBlockEntity) blockentity);
				}
				level.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, level, pos, p_48716_, p_48717_);
		}
	}

	public boolean hasAnalogOutputSignal(BlockState p_48700_) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState p_48702_, Level p_48703_, BlockPos p_48704_) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_48703_.getBlockEntity(p_48704_));
	}

	public RenderShape getRenderShape(BlockState p_48727_) {
		return RenderShape.MODEL;
	}

	public BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
		return p_48722_.setValue(FACING, p_48723_.rotate(p_48722_.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror p_48720_) {
		return state.rotate(p_48720_.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48725_) {
		p_48725_.add(FACING, LIT);
	}
}