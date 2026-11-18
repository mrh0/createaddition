package com.mrh0.createaddition.blocks.rotor;

import com.mrh0.createaddition.blocks.stator.StatorBlock;
import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RotorBlock extends AbstractSimpleShaftBlock {

	public RotorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AllShapes.SIX_VOXEL_POLE.get(state.getValue(AXIS));
	}

	@Override
	public float getParticleTargetRadius() {
		return .35f;
	}

	@Override
	public float getParticleInitialRadius() {
		return .125f;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemInteractionResult result = StatorBlock.tryPlacementHelper(stack, state, level, pos, player, hand, hitResult);
		if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
			return result;
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		boolean sameRotor = newState.getBlock() instanceof RotorBlock && newState.getValue(AXIS) == state.getValue(AXIS);
		if (!level.isClientSide && !isMoving && !sameRotor)
			StatorBlock.breakRingOf(level, pos, state.getValue(AXIS));
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
		return CABlockEntities.ROTOR.get();
	}
}
