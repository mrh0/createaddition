package com.mrh0.createaddition.blocks.cake;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

public class CACakeBlock extends CakeBlock {

	public CACakeBlock(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult ray) {
		if (world.isClientSide) {
			ItemStack itemstack = player.getItemInHand(hand);
			if (eat(world, pos, state, player).consumesAction())
				return InteractionResult.SUCCESS;
			if (itemstack.isEmpty())
				return InteractionResult.CONSUME;
		}
		return eat(world, pos, state, player);
	}
	
	protected static InteractionResult eat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
		if (!player.canEat(false))
			return InteractionResult.PASS;
		else {
			player.awardStat(Stats.EAT_CAKE_SLICE);
			player.getFoodData().eat(3, 0.3F);
			int i = state.getValue(BITES);
			if (i < 6)
				world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
			else
				world.removeBlock(pos, false);
			return InteractionResult.SUCCESS;
		}
	}
}
