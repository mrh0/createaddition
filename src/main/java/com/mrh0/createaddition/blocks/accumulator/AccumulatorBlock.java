package com.mrh0.createaddition.blocks.accumulator;
/*
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.ITransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AccumulatorBlock extends Block implements IBE<AccumulatorBlockEntity>, IWrenchable, ITransformableBlock {

	public static final VoxelShape ACCUMULATOR_SHAPE_MAIN = Block.box(0, 0, 0, 16, 12, 16);
	public static final VoxelShape ACCUMULATOR_SHAPE_X = Shapes.or(ACCUMULATOR_SHAPE_MAIN, Block.box(1, 0, 6, 5, 16, 10), Block.box(11, 0, 6, 15, 16, 10));
	public static final VoxelShape ACCUMULATOR_SHAPE_Z = Shapes.or(ACCUMULATOR_SHAPE_MAIN, Block.box(6, 0, 1, 10, 16, 5), Block.box(6, 0, 11, 10, 16, 15));
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public AccumulatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(NodeRotation.ROTATION, NodeRotation.NONE));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worlIn, BlockPos pos, CollisionContext context) {
		Axis axis = state.getValue(FACING).getAxis();
		return axis == Axis.X ? ACCUMULATOR_SHAPE_X : ACCUMULATOR_SHAPE_Z;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, NodeRotation.ROTATION);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		return defaultBlockState().setValue(FACING, c.getPlayer().isShiftKeyDown() ? c.getHorizontalDirection().getCounterClockWise() : c.getHorizontalDirection().getClockWise());
	}
	
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		BlockEntity te = world.getBlockEntity(pos);
		if(te != null) {
			if(te instanceof AccumulatorBlockEntity) {
				AccumulatorBlockEntity ate = (AccumulatorBlockEntity) te;
				if(stack.hasTag()) {
					CompoundTag nbt = stack.getTag();
					if(nbt.contains("energy"))
						ate.setEnergy(nbt.getInt("energy"));
				
				}
			}
		}
		super.setPlacedBy(world, pos, state, entity, stack);
	}
	
	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(worldIn, pos, state, player);

		if (worldIn.isClientSide()) return;
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null) return;
		if (!(te instanceof IWireNode cte)) return;
		cte.dropWires(worldIn, !player.isCreative());
	}
	
	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext c) {
		BlockEntity te = c.getLevel().getBlockEntity(c.getClickedPos());
		if(te == null)
			return IWrenchable.super.onSneakWrenched(state, c);
		if(!(te instanceof IWireNode))
			return IWrenchable.super.onSneakWrenched(state, c);
		IWireNode cte = (IWireNode) te;

		if (!c.getLevel().isClientSide())
			cte.dropWires(c.getLevel(), c.getPlayer(), !c.getPlayer().isCreative());

		return IWrenchable.super.onSneakWrenched(state, c);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return IComparatorOverride.getComparetorOverride(worldIn, pos);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
		return rotate(state, direction);
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}

	@Override
	public BlockState transform(BlockState state, StructureTransform transform) {
		NodeRotation rotation = NodeRotation.get(transform.rotationAxis, transform.rotation);
		// Handle default rotation & mirroring.
		if (transform.mirror != null) state = mirror(state, transform.mirror);
		if (transform.rotationAxis == Axis.Y) state = rotate(state, transform.rotation);
		// Set the rotation state, which will be used to update the nodes.
		return state.setValue(NodeRotation.ROTATION, rotation);
	}

	@Override
	public Class<AccumulatorBlockEntity> getBlockEntityClass() {
		return AccumulatorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends AccumulatorBlockEntity> getBlockEntityType() {
		return CABlockEntities.ACCUMULATOR.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.ACCUMULATOR.create(pos, state);
	}
}
*/