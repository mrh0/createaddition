package com.mrh0.createaddition.blocks.heater;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import net.minecraft.block.AbstractBlock.Properties;

public class HeaterBlock extends Block implements ITE<HeaterTileEntity>, IWrenchable {
	
	public static final VoxelShaper HEATER_SHAPE = CAShapes.shape(4, 0, 4, 12, 13, 12).add(3, 0, 3, 13, 2, 13).add(5, 0, 5, 11, 16, 11)
			.add(3, 3, 3, 13, 7, 13).add(3, 8, 3, 13, 12, 13).forDirectional();
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public HeaterBlock(Properties properties) {
		super(properties);
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return HEATER_SHAPE.get(state.getValue(FACING).getOpposite());
	}

	@Override
	public Class<HeaterTileEntity> getTileEntityClass() {
		return HeaterTileEntity.class;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.HEATER.create();
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return this.defaultBlockState().setValue(FACING, c.getClickedFace().getOpposite());
	}
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		TileEntity te = world.getBlockEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof HeaterTileEntity))
			return;
		HeaterTileEntity hte = (HeaterTileEntity) te;
		hte.refreshCache();
	}
}
