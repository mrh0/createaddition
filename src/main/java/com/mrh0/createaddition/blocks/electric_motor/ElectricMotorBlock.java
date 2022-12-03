package com.mrh0.createaddition.blocks.electric_motor;

import java.util.Random;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ElectricMotorBlock extends DirectionalKineticBlock implements ITE<ElectricMotorTileEntity> {

	public static final VoxelShaper ELECTRIC_MOTOR_SHAPE = CAShapes.shape(0, 5, 0, 16, 11, 16).add(3, 0, 3, 13, 14, 13)
			.forDirectional();
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public ElectricMotorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ELECTRIC_MOTOR_SHAPE.get(state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferred = getPreferredFacing(context);
		if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || preferred == null)
			return super.getStateForPlacement(context);
		return defaultBlockState().setValue(FACING, preferred);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.ELECTRIC_MOTOR.create(pos, state);
	}

	@Override
	public Class<ElectricMotorTileEntity> getTileEntityClass() {
		return ElectricMotorTileEntity.class;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}

	public void setPowered(Level world, BlockPos pos, boolean powered) {
		world.setBlock(pos, world.getBlockState(pos).setValue(POWERED, powered), 3);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public BlockEntityType<? extends ElectricMotorTileEntity> getTileEntityType() {
		return CATileEntities.ELECTRIC_MOTOR.get();
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos from, boolean b) {
		if (!world.isClientSide) {
			boolean flag = state.getValue(POWERED);
			if (flag != world.hasNeighborSignal(pos)) {
				if (flag)
					world.scheduleTick(pos, this, 4);
				else
					world.setBlock(pos, state.cycle(POWERED), 2);
			}
		}
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
			world.setBlock(pos, state.cycle(POWERED), 2);
	}
}
