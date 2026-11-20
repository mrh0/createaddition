package com.mrh0.createaddition.blocks.stator;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.mrh0.createaddition.blocks.rotor.RotorBlock;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StatorBlock extends Block {

	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
	public static final EnumProperty<StatorType> TYPE = EnumProperty.create("type", StatorType.class);
	public static final IntegerProperty ROLL = IntegerProperty.create("roll", 0, 3);

	public static final int RING_SIZE = 8;

	public static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new StatorPlacementHelper());

	private static final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

	public StatorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(AXIS, Axis.Z)
				.setValue(TYPE, StatorType.SIDE)
				.setValue(ROLL, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(AXIS, TYPE, ROLL));
	}

	public static StatorType typeOfRingIndex(int index) {
		return (index & 1) == 0 ? StatorType.SIDE : StatorType.CORNER;
	}

	public static int rollOfRingIndex(int index) {
		return (index >> 1) & 3;
	}

	public static int ringIndex(BlockState state) {
		return state.getValue(ROLL) * 2 + (state.getValue(TYPE) == StatorType.CORNER ? 1 : 0);
	}

	public BlockState stateFor(Axis axis, int ringIndex) {
		return defaultBlockState()
				.setValue(AXIS, axis)
				.setValue(TYPE, typeOfRingIndex(ringIndex))
				.setValue(ROLL, rollOfRingIndex(ringIndex));
	}

	/** Offset from the rotor to the stator, in world space. */
	public static BlockPos ringOffset(Axis axis, int ringIndex) {
		int x = typeOfRingIndex(ringIndex) == StatorType.CORNER ? 1 : 0;
		int y = -1;
		for (int i = 0; i < rollOfRingIndex(ringIndex); i++) {
			int t = x;
			x = -y;
			y = t;
		}
		return toWorld(axis, x, y, 0);
	}

	/**
	 * Maps a vector in the model frame (rotor along Z) into the world frame for the given axis.
	 * Must match the blockstate rotations: axis Y uses x=90, axis X uses y=90.
	 */
	private static BlockPos toWorld(Axis axis, int x, int y, int z) {
		return switch (axis) {
			case Z -> new BlockPos(x, y, z);
			case Y -> new BlockPos(x, z, -y);
			case X -> new BlockPos(-z, y, x);
		};
	}

	public static BlockPos rotorPos(BlockState state, BlockPos statorPos) {
		return statorPos.subtract(ringOffset(state.getValue(AXIS), ringIndex(state)));
	}

	public static boolean isRotor(BlockState state, Axis axis) {
		return state.getBlock() instanceof RotorBlock && state.getValue(RotorBlock.AXIS) == axis;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelReader level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		// Normal (sneak) placement: prefer the rotor the player clicked against, then any rotor this position surrounds.
		BlockPos against = pos.relative(context.getClickedFace().getOpposite());
		BlockState found = null;
		for (Axis axis : Axis.values()) {
			for (int i = 0; i < RING_SIZE; i++) {
				BlockPos rotor = pos.subtract(ringOffset(axis, i));
				if (!isRotor(level.getBlockState(rotor), axis))
					continue;
				BlockState state = stateFor(axis, i);
				if (rotor.equals(against))
					return state;
				if (found == null || (found.getValue(TYPE) == StatorType.CORNER && state.getValue(TYPE) == StatorType.SIDE))
					found = state;
			}
		}
		return found;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		return tryPlacementHelper(stack, state, level, pos, player, hand, hitResult);
	}

	/** Shared by rotor and stator: lets the placement helper place the next stator in the ring. */
	public static ItemInteractionResult tryPlacementHelper(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (player.isShiftKeyDown() || !player.mayBuild() || !(stack.getItem() instanceof BlockItem blockItem))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		IPlacementHelper helper = PlacementHelpers.get(PLACEMENT_HELPER_ID);
		if (!helper.matchesItem(stack))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		return helper.getOffset(player, level, state, pos, hitResult)
				.placeInWorld(level, blockItem, player, hand, hitResult);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return isRotor(level.getBlockState(rotorPos(state, pos)), state.getValue(AXIS));
	}

	/**
	 * Called by the rotor when it is removed or changes axis, since corner stators are diagonal
	 * to the rotor and never receive neighbour updates from it.
	 */
	public static void breakRingOf(net.minecraft.world.level.Level level, BlockPos rotorPos, Axis axis) {
		for (int i = 0; i < RING_SIZE; i++) {
			BlockPos pos = rotorPos.offset(ringOffset(axis, i));
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof StatorBlock && state.getValue(AXIS) == axis && ringIndex(state) == i)
				level.destroyBlock(pos, true);
		}
	}

	/** Number of correctly placed stators (0-8) around the rotor. Skips unloaded positions. */
	public static int countRing(Level level, BlockPos rotorPos, Axis axis) {
		int count = 0;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < RING_SIZE; i++) {
			pos.setWithOffset(rotorPos, ringOffset(axis, i));
			if (!level.isLoaded(pos))
				continue;
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof StatorBlock && state.getValue(AXIS) == axis && ringIndex(state) == i)
				count++;
		}
		return count;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.computeIfAbsent(state, StatorBlock::computeShape);
	}

	private static final int[][] SIDE_BOXES = { { 0, 0, 16, 6 } };
	private static final int[][] CORNER_BOXES = { { 0, 0, 4, 8 }, { 4, 0, 8, 12 }, { 8, 4, 12, 16 }, { 12, 8, 16, 16 } };

	private static VoxelShape computeShape(BlockState state) {
		int[][] boxes = state.getValue(TYPE) == StatorType.CORNER ? CORNER_BOXES : SIDE_BOXES;
		Axis axis = state.getValue(AXIS);
		int roll = state.getValue(ROLL);
		VoxelShape shape = Shapes.empty();
		for (int[] b : boxes) {
			// Work centred on the block (doubled to stay in integers), roll around Z, then map to the axis.
			int[] a = transformCorner(axis, roll, b[0], b[1], 0);
			int[] c = transformCorner(axis, roll, b[2], b[3], 16);
			shape = Shapes.or(shape, Block.box(
					Math.min(a[0], c[0]), Math.min(a[1], c[1]), Math.min(a[2], c[2]),
					Math.max(a[0], c[0]), Math.max(a[1], c[1]), Math.max(a[2], c[2])));
		}
		return shape.optimize();
	}

	private static int[] transformCorner(Axis axis, int roll, int px, int py, int pz) {
		int x = px - 8, y = py - 8, z = pz - 8;
		for (int i = 0; i < roll; i++) {
			int t = x;
			x = -y;
			y = t;
		}
		BlockPos w = toWorld(axis, x, y, z);
		return new int[] { w.getX() + 8, w.getY() + 8, w.getZ() + 8 };
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}
}
