package com.mrh0.createaddition.blocks.redstone_relay;

import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.ITransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

public class RedstoneRelayBlock extends Block implements IBE<RedstoneRelayBlockEntity>, IWrenchable, ITransformableBlock {

	public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShape HORIZONTAL_SHAPE_MAIN = Block.box(0, 0, 0, 16, 2, 16);
	public static final VoxelShape HORIZONTAL_SHAPE_X = Shapes.or(HORIZONTAL_SHAPE_MAIN, Block.box(1, 0, 6, 5, 7, 10), Block.box(11, 0, 6, 15, 7, 10));
	public static final VoxelShape HORIZONTAL_SHAPE_Z = Shapes.or(HORIZONTAL_SHAPE_MAIN, Block.box(6, 0, 1, 10, 7, 5), Block.box(6, 0, 11, 10, 7, 15));

	//public static final VoxelShaper VERTICAL_SHAPE = CAShapes.shape(0, 0, 14, 16, 16, 16).add(1, 6, 9, 5, 10, 16).add(11, 6, 9, 15, 10, 16).forDirectional();

	public static final VoxelShaper VERTICAL_SHAPE = CAShapes.shape(0, 0, 0, 16, 2, 16).add(1, 0, 6, 5, 7, 10).add(11, 0, 6, 15, 7, 10).forDirectional();

	protected static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 2, 16, 16);
	protected static final VoxelShape EAST_SHAPE = Block.box(14, 0, 0, 16, 16, 16);
	protected static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 2);
	protected static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 14, 16, 16, 16);


	public RedstoneRelayBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(VERTICAL, false)
				.setValue(HORIZONTAL_FACING, Direction.NORTH)
				.setValue(POWERED, false)
				.setValue(NodeRotation.ROTATION, NodeRotation.NONE));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction dir = state.getValue(HORIZONTAL_FACING);
		if(state.getValue(VERTICAL))
			return VERTICAL_SHAPE.get(dir.getOpposite());
		Axis axis = dir.getAxis();
		return axis == Axis.X ? HORIZONTAL_SHAPE_X : HORIZONTAL_SHAPE_Z;
	}

	@Override
	public Class<RedstoneRelayBlockEntity> getBlockEntityClass() {
		return RedstoneRelayBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends RedstoneRelayBlockEntity> getBlockEntityType() {
		return CABlockEntities.REDSTONE_RELAY.get();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(VERTICAL, HORIZONTAL_FACING, POWERED, NodeRotation.ROTATION);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		if(c.getClickedFace().getAxis() == Axis.Y)
			return defaultBlockState().setValue(HORIZONTAL_FACING, c.getPlayer().isShiftKeyDown() ? c.getHorizontalDirection().getCounterClockWise() : c.getHorizontalDirection().getClockWise()).setValue(VERTICAL, false);
		else
			return defaultBlockState().setValue(HORIZONTAL_FACING, c.getClickedFace().getOpposite()).setValue(VERTICAL, true);
	}

	@Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
		boolean flag = state.getValue(POWERED);
		boolean flag1 = this.shouldBePowered(worldIn, pos, state);
		if (flag && !flag1) {
			worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(false)), 2);
		}
		else if (!flag) {
			worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(true)), 2);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (state.canSurvive(worldIn, pos))
			this.updateState(worldIn, pos, state);
		else {
			BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
			dropResources(state, worldIn, pos, tileentity);
			worldIn.removeBlock(pos, false);

			for (Direction direction : Direction.values())
				worldIn.updateNeighborsAt(pos.relative(direction), this);
		}
	}

	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		boolean vertical = state.getValue(VERTICAL);
		Direction direction = state.getValue(HORIZONTAL_FACING);
		return canSupportCenter(world, vertical ? pos.relative(direction) : pos.below(), vertical ? direction.getOpposite() : Direction.UP);
	}

	protected void updateState(Level worldIn, BlockPos pos, BlockState state) {
		boolean flag = state.getValue(POWERED);
		boolean flag1 = this.shouldBePowered(worldIn, pos, state);
		if (flag != flag1 && !worldIn.getBlockTicks().willTickThisTick(pos, this)) {
			TickPriority tickpriority = TickPriority.VERY_HIGH;

			worldIn.scheduleTick(pos, this, this.getDelay(state), tickpriority);
		}
	}

	private int getDelay(BlockState state) {
		return 2;
	}

	protected boolean shouldBePowered(Level worldIn, BlockPos pos, BlockState state) {
		return this.calculateInputStrength(worldIn, pos, state) > 0;
	}

	protected int calculateInputStrength(Level worldIn, BlockPos pos, BlockState state) {
		boolean vertical = state.getValue(VERTICAL);

		if(vertical) {
			BlockPos blockpos1 = pos.relative(Direction.UP);
			BlockPos blockpos2 = pos.relative(Direction.DOWN);
			int i = Math.max(worldIn.getSignal(blockpos1, Direction.DOWN), worldIn.getSignal(blockpos2, Direction.UP));

			BlockState blockstate1 = worldIn.getBlockState(blockpos1);
			BlockState blockstate2 = worldIn.getBlockState(blockpos2);
			return Math.max(i, Math.max(blockstate1.is(Blocks.REDSTONE_WIRE) ? blockstate1.getValue(RedStoneWireBlock.POWER) : 0, blockstate2.is(Blocks.REDSTONE_WIRE) ? blockstate2.getValue(RedStoneWireBlock.POWER) : 0));
		}
		else {
			Direction direction = state.getValue(HORIZONTAL_FACING);
			BlockPos blockpos1 = pos.relative(direction.getClockWise());
			BlockPos blockpos2 = pos.relative(direction.getCounterClockWise());
			int i = Math.max(worldIn.getSignal(blockpos1, direction.getClockWise()), worldIn.getSignal(blockpos2, direction.getCounterClockWise()));
			int j = Math.max(worldIn.getDirectSignal(blockpos1, direction.getClockWise()), worldIn.getDirectSignal(blockpos2, direction.getCounterClockWise()));

			BlockState blockstate1 = worldIn.getBlockState(blockpos1);
			BlockState blockstate2 = worldIn.getBlockState(blockpos2);
			return Math.max(Math.max(i, j), Math.max(blockstate1.is(Blocks.REDSTONE_WIRE) ? blockstate1.getValue(RedStoneWireBlock.POWER) : 0, blockstate2.is(Blocks.REDSTONE_WIRE) ? blockstate2.getValue(RedStoneWireBlock.POWER) : 0));
		}
	}
	protected int getPowerOnSides(LevelReader worldIn, BlockPos pos, Direction direction) {
		Direction direction1 = direction.getClockWise();
		Direction direction2 = direction.getCounterClockWise();
		return Math.max(this.getPowerOnSide(worldIn, pos.relative(direction1), direction2), this.getPowerOnSide(worldIn, pos.relative(direction2), direction1));
	}

	protected int getPowerOnSide(LevelReader worldIn, BlockPos pos, Direction side) {
		BlockState blockstate = worldIn.getBlockState(pos);
		if (this.isAlternateInput(blockstate)) {
			if (blockstate.is(Blocks.REDSTONE_BLOCK))
				return 15;
			else
				return blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedStoneWireBlock.POWER) : worldIn.getDirectSignal(pos, side);
		} else
			return 0;
	}

	protected boolean isAlternateInput(BlockState state) {
		return state.isSignalSource();
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (this.shouldBePowered(worldIn, pos, state)) {
			worldIn.scheduleTick(pos, this, 1);
		}

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
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		if(pos == null || side == null || state == null || world == null)
			return false;
		return !state.getValue(VERTICAL).booleanValue() && side.getAxis() != state.getValue(HORIZONTAL_FACING).getAxis();
	}

	private BlockState fromRotation(BlockState state, Direction dir) {
		return state.setValue(HORIZONTAL_FACING, dir);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		return fromRotation(state, direction.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
		return rotate(state, direction);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return fromRotation(state, mirror.mirror(state.getValue(HORIZONTAL_FACING)));
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.REDSTONE_RELAY.create(pos, state);
	}
}
