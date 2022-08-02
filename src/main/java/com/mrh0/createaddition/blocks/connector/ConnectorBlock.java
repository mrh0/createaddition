package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectorBlock extends Block implements ITE<ConnectorTileEntity>, IWrenchable {

	public static final VoxelShaper CONNECTOR_SHAPE = CAShapes.shape(6, 0, 6, 10, 5, 10).forDirectional();
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape boxwe = Block.box(0,7,7,10,9,9);
	private static final VoxelShape boxsn = Block.box(7,7,0,9,9,10);

	public ConnectorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return CONNECTOR_SHAPE.get(state.getValue(FACING).getOpposite());
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.CONNECTOR.create(pos, state);
	}

	@Override
	public Class<ConnectorTileEntity> getTileEntityClass() {
		return ConnectorTileEntity.class;
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
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(worldIn, pos, state, player);
		if(player.isCreative())
			return;
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te == null)
			return;
		if(!(te instanceof IWireNode))
			return;
		IWireNode cte = (IWireNode) te;
		
		cte.dropWires(worldIn);
	}
	
	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext c) {
		if(c.getPlayer().isCreative())
			return IWrenchable.super.onSneakWrenched(state, c);
		BlockEntity te = c.getLevel().getBlockEntity(c.getClickedPos());
		if(te == null)
			return IWrenchable.super.onSneakWrenched(state, c);
		if(!(te instanceof IWireNode))
			return IWrenchable.super.onSneakWrenched(state, c);
		IWireNode cte = (IWireNode) te;
		
		cte.dropWires(c.getLevel(), c.getPlayer());
		return IWrenchable.super.onSneakWrenched(state, c);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		if(tileentity != null) {
			if(tileentity instanceof ConnectorTileEntity) {
				((ConnectorTileEntity)tileentity).updateCache();
			}
		}
		if (!state.canSurvive(worldIn, pos)) {
			dropResources(state, worldIn, pos, tileentity);
			
			if(tileentity instanceof IWireNode)
				((IWireNode) tileentity).dropWires(worldIn);
			
			worldIn.removeBlock(pos, false);

			for (Direction direction : Direction.values())
				worldIn.updateNeighborsAt(pos.relative(direction), this);
		}
	}
	
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction dir = state.getValue(FACING);
		return
				!Shapes.joinIsNotEmpty(world.getBlockState(pos.relative(dir)).getBlockSupportShape(world,pos.relative(dir)).getFaceShape(dir.getOpposite()), boxwe, BooleanOp.ONLY_SECOND) ||
				!Shapes.joinIsNotEmpty(world.getBlockState(pos.relative(dir)).getBlockSupportShape(world,pos.relative(dir)).getFaceShape(dir.getOpposite()), boxsn, BooleanOp.ONLY_SECOND) ||
				world.getBlockState(pos.relative(dir)).isFaceSturdy(world, pos, dir.getOpposite(), SupportType.CENTER);
	}

	@Override
	public BlockEntityType<? extends ConnectorTileEntity> getTileEntityType() {
		return CATileEntities.CONNECTOR.get();
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean b) {
		if(!world.isClientSide()) {
			BlockEntity be = world.getBlockEntity(pos);
			if(be != null && !(newState.getBlock() instanceof ConnectorBlock))
				if(be instanceof ConnectorTileEntity)
					((ConnectorTileEntity) be).onBlockRemoved();
		}
		super.onRemove(state, world, pos, newState, b);
	}
}
