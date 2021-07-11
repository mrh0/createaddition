package com.mrh0.createaddition.blocks.cake;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class Cake extends CakeBlock {

	public Cake(Properties props) {
		super(props);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
		if (world.isClientSide) {
			ItemStack itemstack = player.getItemInHand(hand);
			if (this.tryEat(world, pos, state, player).consumesAction())
				return ActionResultType.SUCCESS;
			if (itemstack.isEmpty())
				return ActionResultType.CONSUME;
		}
		return this.tryEat(world, pos, state, player);
	}

	private ActionResultType tryEat(IWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!player.canEat(false))
			return ActionResultType.PASS;
		else {
			player.awardStat(Stats.EAT_CAKE_SLICE);
			player.getFoodData().eat(3, 0.3F);
			int i = state.getValue(BITES);
			if (i < 6)
				world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
			else
				world.removeBlock(pos, false);
			return ActionResultType.SUCCESS;
		}
	}
}
