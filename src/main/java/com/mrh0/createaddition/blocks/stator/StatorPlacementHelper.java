package com.mrh0.createaddition.blocks.stator;

import java.util.function.Predicate;

import com.mrh0.createaddition.blocks.rotor.RotorBlock;
import com.mrh0.createaddition.index.CABlocks;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class StatorPlacementHelper implements IPlacementHelper {

	@Override
	public Predicate<ItemStack> getItemPredicate() {
		return CABlocks.STATOR::isIn;
	}

	@Override
	public Predicate<BlockState> getStatePredicate() {
		return state -> state.getBlock() instanceof RotorBlock || state.getBlock() instanceof StatorBlock;
	}

	@Override
	public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos, BlockHitResult ray) {
		Direction face = ray.getDirection();
		Axis axis;
		BlockPos rotorPos;
		int startIndex = 0;

		if (state.getBlock() instanceof RotorBlock) {
			axis = state.getValue(RotorBlock.AXIS);
			rotorPos = pos;
			// Start at the slot on the clicked face when clicking the side of the rotor.
			for (int i = 0; i < StatorBlock.RING_SIZE; i += 2)
				if (StatorBlock.ringOffset(axis, i).equals(BlockPos.ZERO.relative(face)))
					startIndex = i;
		} else if (state.getBlock() instanceof StatorBlock) {
			axis = state.getValue(StatorBlock.AXIS);
			rotorPos = StatorBlock.rotorPos(state, pos);
			if (!StatorBlock.isRotor(level.getBlockState(rotorPos), axis))
				return PlacementOffset.fail();
			startIndex = StatorBlock.ringIndex(state) + 1;
		} else {
			return PlacementOffset.fail();
		}

		for (int k = 0; k < StatorBlock.RING_SIZE; k++) {
			int index = (startIndex + k) % StatorBlock.RING_SIZE;
			BlockPos target = rotorPos.offset(StatorBlock.ringOffset(axis, index));
			if (!level.isInWorldBounds(target) || !level.getBlockState(target).canBeReplaced())
				continue;
			return PlacementOffset.success(target, s -> s.getBlock() instanceof StatorBlock stator
					? stator.stateFor(axis, index) : s);
		}
		return PlacementOffset.fail();
	}
}
