package com.mrh0.createaddition.blocks.connector.base;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.IWireNode;
import com.mrh0.createaddition.energy.NodeRotation;
import com.simibubi.create.content.contraptions.ITransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractConnectorBlock<BE extends AbstractConnectorBlockEntity> extends Block implements IBE<BE>, IWrenchable, ITransformableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final EnumProperty<ConnectorMode> MODE = EnumProperty.create("mode", ConnectorMode.class);
	public static final EnumProperty<ConnectorVariant> VARIANT = EnumProperty.create("variant", ConnectorVariant.class);
	private static final VoxelShape boxwe = Block.box(0,7,7,10,9,9);
	private static final VoxelShape boxsn = Block.box(7,7,0,9,9,10);

	public AbstractConnectorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(MODE, ConnectorMode.None)
				.setValue(NodeRotation.ROTATION, NodeRotation.NONE)
				.setValue(VARIANT, ConnectorVariant.Default));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE, NodeRotation.ROTATION, VARIANT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		Direction dir = c.getClickedFace().getOpposite();
		ConnectorMode mode = ConnectorMode.test(c.getLevel(), c.getClickedPos().relative(dir), c.getClickedFace());
		ConnectorVariant variant = ConnectorVariant.test(c.getLevel(), c.getClickedPos().relative(dir), c.getClickedFace());
		return this.defaultBlockState().setValue(FACING, dir).setValue(MODE, mode).setValue(VARIANT, variant);
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
	public InteractionResult onWrenched(BlockState state, UseOnContext c) {
		if (c.getLevel().isClientSide()) {
			c.getLevel().playLocalSound(c.getClickedPos().getX(), c.getClickedPos().getY(), c.getClickedPos().getZ(), SoundEvents.BONE_BLOCK_HIT, SoundSource.BLOCKS, 1f, 1f, false);
		}
		c.getLevel().setBlockAndUpdate(c.getClickedPos(), state.setValue(MODE, state.getValue(MODE).getNext()));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext c) {
		BlockEntity be = c.getLevel().getBlockEntity(c.getClickedPos());
		if(be == null) return IWrenchable.super.onSneakWrenched(state, c);
		if(!(be instanceof IWireNode cbe)) return IWrenchable.super.onSneakWrenched(state, c);
		// if(be instanceof AbstractConnectorBlockEntity acbe) acbe.updateExternalEnergyStorage();

		if (!c.getLevel().isClientSide() && c.getPlayer() != null)
			cbe.dropWires(c.getLevel(), c.getPlayer(), !c.getPlayer().isCreative());

		return IWrenchable.super.onSneakWrenched(state, c);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		if(blockEntity != null) {
			if(blockEntity instanceof AbstractConnectorBlockEntity) {
				((AbstractConnectorBlockEntity)blockEntity).updateExternalEnergyStorage();
			}
		}
		if (!state.canSurvive(worldIn, pos)) {
			dropResources(state, worldIn, pos, blockEntity);

			if(blockEntity instanceof IWireNode)
				((IWireNode) blockEntity).dropWires(worldIn, true);

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
				world.getBlockState(pos.relative(dir)).isFaceSturdy(world, pos, dir.getOpposite(), SupportType.CENTER) || Config.CONNECTOR_IGNORE_FACE_CHECK.get();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		// Handle old rotation.
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
		state = state.setValue(FACING, rotation.rotate(state.getValue(FACING), false));
		// Set the rotation state, which will be used to update the nodes.
		return state.setValue(NodeRotation.ROTATION, rotation);
	}
}
