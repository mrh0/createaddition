package com.mrh0.createaddition.blocks.tesla_coil;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class TeslaCoil extends Block implements ITE<TeslaCoilTileEntity>, IWrenchable {
	public TeslaCoil(Properties props) {
		super(props);
	}

	//public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

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
	
	/*private TeslaCoilTileEntity getTE(BlockState state, World world, BlockPos pos) {
		TileEntity tileentity;
		if(isLower(state))
			tileentity = world.getBlockEntity(pos);
		else
			tileentity = world.getBlockEntity(pos.below());
		if (tileentity != null && tileentity instanceof TeslaCoilTileEntity)
			return (TeslaCoilTileEntity) tileentity;
		return null;
	}*/
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return super.hasTileEntity(state);
	}
	
	/*public static boolean isLower(BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER;
	}*/
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return this.defaultBlockState().setValue(FACING, c.getClickedFace().getOpposite());
	}
}
