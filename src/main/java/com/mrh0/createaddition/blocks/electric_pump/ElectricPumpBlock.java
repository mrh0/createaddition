package com.mrh0.createaddition.blocks.electric_pump;

import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

public class ElectricPumpBlock extends PumpBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final VoxelShaper ELECTRIC_PUMP_SHAPE = CAShapes.shape(0, 5, 0, 16, 11, 16).forDirectional();

	public ElectricPumpBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return ELECTRIC_PUMP_SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public boolean isSmallCog() {
		return false;
	}

	@Override
	public boolean isLargeCog() {
		return false;
	}

	@Override
	public boolean isDedicatedCogWheel() {
		return false;
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		AdvancementBehaviour.setPlacedBy(worldIn, pos, placer);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos,
			boolean isMoving) {
		super.neighborChanged(state, world, pos, otherBlock, neighborPos, isMoving);

		if (!world.isClientSide) {
			boolean neighborSignal = world.hasNeighborSignal(pos);
			BlockState current = world.getBlockState(pos);
			if (current.getValue(POWERED) != neighborSignal)
				world.setBlock(pos, current.setValue(POWERED, neighborSignal), Block.UPDATE_ALL);
		}
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
		return true;
	}

	@Override
	public BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
		return CABlockEntities.ELECTRIC_PUMP.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.ELECTRIC_PUMP.create(pos, state);
	}
}
