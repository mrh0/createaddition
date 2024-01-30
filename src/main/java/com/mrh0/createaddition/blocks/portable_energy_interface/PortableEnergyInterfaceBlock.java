package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortableEnergyInterfaceBlock extends WrenchableDirectionalBlock implements IBE<PortableEnergyInterfaceBlockEntity> {

	public PortableEnergyInterfaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
		this.withBlockEntityDo(world, pos, PortableEnergyInterfaceBlockEntity::neighbourChanged);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getNearestLookingDirection();
		if (context.getPlayer() != null && context.getPlayer().isSteppingCarefully()) {
			direction = direction.getOpposite();
		}

		return this.defaultBlockState().setValue(FACING, direction.getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.PORTABLE_STORAGE_INTERFACE.get(state.getValue(FACING));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return this.getBlockEntityOptional(worldIn, pos).map((te) -> te.isConnected() ? 15 : 0).orElse(0);
	}

	@Override
	public Class<PortableEnergyInterfaceBlockEntity> getBlockEntityClass() {
		return PortableEnergyInterfaceBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends PortableEnergyInterfaceBlockEntity> getBlockEntityType() {
		return CABlockEntities.PORTABLE_ENERGY_INTERFACE.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.PORTABLE_ENERGY_INTERFACE.create(pos, state);
	}
}
