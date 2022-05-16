package com.mrh0.createaddition.blocks.heater;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import io.github.fabricators_of_create.porting_lib.block.NeighborChangeListeningBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeaterBlock extends Block implements ITE<HeaterTileEntity>, IWrenchable, NeighborChangeListeningBlock {
	
	public static final VoxelShaper HEATER_SHAPE = CAShapes.shape(4, 0, 4, 12, 13, 12).add(3, 0, 3, 13, 2, 13).add(5, 0, 5, 11, 16, 11)
			.add(3, 3, 3, 13, 7, 13).add(3, 8, 3, 13, 12, 13).forDirectional();
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public HeaterBlock(Properties properties) {
		super(properties);
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return HEATER_SHAPE.get(state.getValue(FACING).getOpposite());
	}

	@Override
	public Class<HeaterTileEntity> getTileEntityClass() {
		return HeaterTileEntity.class;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.HEATER.create(pos, state);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		return this.defaultBlockState().setValue(FACING, c.getClickedFace().getOpposite());
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		BlockEntity te = world.getBlockEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof HeaterTileEntity))
			return;
		HeaterTileEntity hte = (HeaterTileEntity) te;
		hte.refreshCache();
	}

	@Override
	public BlockEntityType<? extends HeaterTileEntity> getTileEntityType() {
		return CATileEntities.HEATER.get();
	}
}
