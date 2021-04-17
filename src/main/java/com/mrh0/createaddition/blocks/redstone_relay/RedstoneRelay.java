package com.mrh0.createaddition.blocks.redstone_relay;

import java.util.Random;

import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneRelay extends Block implements ITE<RedstoneRelayTileEntity>, IWrenchable {

	public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public static final VoxelShape HORIZONTAL_SHAPE_MAIN = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
	public static final VoxelShape HORIZONTAL_SHAPE_X = VoxelShapes.or(HORIZONTAL_SHAPE_MAIN, Block.makeCuboidShape(1, 0, 6, 5, 7, 10), Block.makeCuboidShape(11, 0, 6, 15, 7, 10));
	public static final VoxelShape HORIZONTAL_SHAPE_Z = VoxelShapes.or(HORIZONTAL_SHAPE_MAIN, Block.makeCuboidShape(6, 0, 1, 10, 7, 5), Block.makeCuboidShape(6, 0, 11, 10, 7, 15));
	
	//public static final VoxelShaper VERTICAL_SHAPE = CAShapes.shape(0, 0, 14, 16, 16, 16).add(1, 6, 9, 5, 10, 16).add(11, 6, 9, 15, 10, 16).forDirectional();
	
	public static final VoxelShaper VERTICAL_SHAPE = CAShapes.shape(0, 0, 0, 16, 2, 16).add(1, 0, 6, 5, 7, 10).add(11, 0, 6, 15, 7, 10).forDirectional();
	
	protected static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0, 0, 0, 2, 16, 16);
	protected static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(14, 0, 0, 16, 16, 16);
	protected static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 16, 2);
	protected static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0, 0, 14, 16, 16, 16);
	
	
	public RedstoneRelay(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(VERTICAL, false).with(HORIZONTAL_FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public Class<RedstoneRelayTileEntity> getTileEntityClass() {
		return RedstoneRelayTileEntity.class;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction dir = state.get(HORIZONTAL_FACING);
		if(state.get(VERTICAL))
			return VERTICAL_SHAPE.get(dir.getOpposite());
		Axis axis = dir.getAxis();
		return axis == Axis.X ? HORIZONTAL_SHAPE_X : HORIZONTAL_SHAPE_Z;
	}
	
	/*@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return getShape(state, world, pos, context);
	}
	
	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos) {
		return getShape(state, world, pos, null);
	}
	
	@Override
	public VoxelShape getSidesShape(BlockState state, IBlockReader world, BlockPos pos) {
		return getShape(state, world, pos, null);
	}
	
	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
		return getShape(state, world, pos, null);
	}*/
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.REDSTONE_RELAY.create();
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(VERTICAL, HORIZONTAL_FACING, POWERED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		if(c.getFace().getAxis() == Axis.Y)
			return getDefaultState().with(HORIZONTAL_FACING, c.getPlayer().isSneaking() ? c.getPlacementHorizontalFacing().rotateYCCW() : c.getPlacementHorizontalFacing().rotateY()).with(VERTICAL, false);
		else
			return getDefaultState().with(HORIZONTAL_FACING, c.getFace().getOpposite()).with(VERTICAL, true);
	}
	
	public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		boolean flag = state.get(POWERED);
		boolean flag1 = this.shouldBePowered(worldIn, pos, state);
		if (flag && !flag1) {
			worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 2);
		} 
		else if (!flag) {
			worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 2);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (state.isValidPosition(worldIn, pos))
			this.updateState(worldIn, pos, state);
		else {
			TileEntity tileentity = state.hasTileEntity() ? worldIn.getTileEntity(pos) : null;
			spawnDrops(state, worldIn, pos, tileentity);
			worldIn.removeBlock(pos, false);

			for (Direction direction : Direction.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
		}
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		boolean vertical = state.get(VERTICAL);
		Direction direction = state.get(HORIZONTAL_FACING);
		return hasEnoughSolidSide(world, vertical ? pos.offset(direction) : pos.down(), vertical ? direction.getOpposite() : Direction.UP);
	}

	protected void updateState(World worldIn, BlockPos pos, BlockState state) {
		boolean flag = state.get(POWERED);
		boolean flag1 = this.shouldBePowered(worldIn, pos, state);
		if (flag != flag1 && !worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
			TickPriority tickpriority = TickPriority.VERY_HIGH;

			worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getDelay(state), tickpriority);
		}
	}

	private int getDelay(BlockState state) {
		return 2;
	}
	
	protected boolean shouldBePowered(World worldIn, BlockPos pos, BlockState state) {
		return this.calculateInputStrength(worldIn, pos, state) > 0;
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, BlockState state) {
		boolean vertical = state.get(VERTICAL);
		
		if(vertical) {
			BlockPos blockpos1 = pos.offset(Direction.UP);
			BlockPos blockpos2 = pos.offset(Direction.DOWN);
			int i = Math.max(worldIn.getRedstonePower(blockpos1, Direction.DOWN), worldIn.getRedstonePower(blockpos2, Direction.UP));
			
			BlockState blockstate1 = worldIn.getBlockState(blockpos1);
			BlockState blockstate2 = worldIn.getBlockState(blockpos2);
			return Math.max(i, Math.max(blockstate1.isIn(Blocks.REDSTONE_WIRE) ? blockstate1.get(RedstoneWireBlock.POWER) : 0, blockstate2.isIn(Blocks.REDSTONE_WIRE) ? blockstate2.get(RedstoneWireBlock.POWER) : 0));
		}
		else {
			Direction direction = state.get(HORIZONTAL_FACING);
			BlockPos blockpos1 = pos.offset(direction.rotateY());
			BlockPos blockpos2 = pos.offset(direction.rotateYCCW());
			int i = Math.max(worldIn.getRedstonePower(blockpos1, direction.rotateY()), worldIn.getRedstonePower(blockpos2, direction.rotateYCCW()));
			int j = Math.max(worldIn.getStrongPower(blockpos1, direction.rotateY()), worldIn.getStrongPower(blockpos2, direction.rotateYCCW()));
			
			BlockState blockstate1 = worldIn.getBlockState(blockpos1);
			BlockState blockstate2 = worldIn.getBlockState(blockpos2);
			return Math.max(Math.max(i, j), Math.max(blockstate1.isIn(Blocks.REDSTONE_WIRE) ? blockstate1.get(RedstoneWireBlock.POWER) : 0, blockstate2.isIn(Blocks.REDSTONE_WIRE) ? blockstate2.get(RedstoneWireBlock.POWER) : 0));
		}
	}
	protected int getPowerOnSides(IWorldReader worldIn, BlockPos pos, Direction direction) {
		Direction direction1 = direction.rotateY();
		Direction direction2 = direction.rotateYCCW();
		return Math.max(this.getPowerOnSide(worldIn, pos.offset(direction1), direction2), this.getPowerOnSide(worldIn, pos.offset(direction2), direction1));
	}

	protected int getPowerOnSide(IWorldReader worldIn, BlockPos pos, Direction side) {
		BlockState blockstate = worldIn.getBlockState(pos);
		if (this.isAlternateInput(blockstate)) {
			if (blockstate.isIn(Blocks.REDSTONE_BLOCK))
				return 15;
			else
				return blockstate.isIn(Blocks.REDSTONE_WIRE) ? blockstate.get(RedstoneWireBlock.POWER) : worldIn.getStrongPower(pos, side);
		} else
			return 0;
	}
	
	protected boolean isAlternateInput(BlockState state) {
		return state.canProvidePower();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (this.shouldBePowered(worldIn, pos, state)) {
			worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
		}

	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		if(player.isCreative())
			return;
		TileEntity te = worldIn.getTileEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof IWireNode))
			return;
		IWireNode cte = (IWireNode) te;
		
		cte.dropWires(worldIn);
	}
	
	@Override
	public ActionResultType onSneakWrenched(BlockState state, ItemUseContext c) {
		if(c.getPlayer().isCreative())
			return IWrenchable.super.onSneakWrenched(state, c);
		TileEntity te = c.getWorld().getTileEntity(c.getPos());
		if(te == null)
			return IWrenchable.super.onSneakWrenched(state, c);
		if(!(te instanceof IWireNode))
			return IWrenchable.super.onSneakWrenched(state, c);
		IWireNode cte = (IWireNode) te;
		
		cte.dropWires(c.getWorld(), c.getPlayer());
		return IWrenchable.super.onSneakWrenched(state, c);
	}
}
