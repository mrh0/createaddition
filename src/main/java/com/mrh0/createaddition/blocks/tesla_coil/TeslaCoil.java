package com.mrh0.createaddition.blocks.tesla_coil;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class TeslaCoil extends Block implements ITE<TeslaCoilTileEntity>, IWrenchable {
	public TeslaCoil(Properties props) {
		super(props);
	}

	public static final VoxelShaper TESLA_COIL_SHAPE = CAShapes.shape(0, 0, 0, 16, 10, 16).add(1, 0, 1, 15, 12, 15).forDirectional();
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return TESLA_COIL_SHAPE.get(state.getValue(FACING).getOpposite());
	}
	
	@Override
	public Class<TeslaCoilTileEntity> getTileEntityClass() {
		return TeslaCoilTileEntity.class;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.TESLA_COIL.create();
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		//builder.add(HALF);
		builder.add(FACING);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return this.defaultBlockState().setValue(FACING, c.getClickedFace().getOpposite());
	}
}
