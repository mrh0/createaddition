package com.mrh0.createaddition.blocks.accumulator;

import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AccumulatorBlock extends Block implements ITE<AccumulatorTileEntity>, IWrenchable {

	public static final VoxelShape ACCUMULATOR_SHAPE_MAIN = Block.makeCuboidShape(0, 0, 0, 16, 12, 16);
	public static final VoxelShape ACCUMULATOR_SHAPE_X = VoxelShapes.or(ACCUMULATOR_SHAPE_MAIN, Block.makeCuboidShape(1, 0, 6, 5, 16, 10), Block.makeCuboidShape(11, 0, 6, 15, 16, 10));
	public static final VoxelShape ACCUMULATOR_SHAPE_Z = VoxelShapes.or(ACCUMULATOR_SHAPE_MAIN, Block.makeCuboidShape(6, 0, 1, 10, 16, 5), Block.makeCuboidShape(6, 0, 11, 10, 16, 15));
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public AccumulatorBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public Class<AccumulatorTileEntity> getTileEntityClass() {
		return AccumulatorTileEntity.class;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		BlockState bs = worldIn.getBlockState(pos);
		if(bs.getBlock() == Blocks.AIR)
			return ACCUMULATOR_SHAPE_X;
		Axis axis = bs.get(FACING).getAxis();
		return axis == Axis.X ? ACCUMULATOR_SHAPE_X : ACCUMULATOR_SHAPE_Z;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.ACCUMULATOR.create();
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return getDefaultState().with(FACING, c.getPlayer().isSneaking() ? c.getPlacementHorizontalFacing().rotateYCCW() : c.getPlacementHorizontalFacing().rotateY());
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		if(player.isCreative())
			return;
		TileEntity te = worldIn.getTileEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof ConnectorTileEntity))
			return;
		AccumulatorTileEntity cte = (AccumulatorTileEntity) te;
		
		cte.dropWires(worldIn);
	}
	
	@Override
	public ActionResultType onSneakWrenched(BlockState state, ItemUseContext c) {
		if(c.getPlayer().isCreative())
			return IWrenchable.super.onSneakWrenched(state, c);
		TileEntity te = c.getWorld().getTileEntity(c.getPos());
		if(te == null)
			return IWrenchable.super.onSneakWrenched(state, c);
		if(!(te instanceof AccumulatorTileEntity))
			return IWrenchable.super.onSneakWrenched(state, c);
		AccumulatorTileEntity cte = (AccumulatorTileEntity) te;
		
		cte.dropWires(c.getWorld(), c.getPlayer());
		return IWrenchable.super.onSneakWrenched(state, c);
	}
}
