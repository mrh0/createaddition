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

public class Cake extends CakeBlock {

	public Cake(Properties props) {
		super(props);
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
		if (world.isRemote) {
			ItemStack itemstack = player.getHeldItem(hand);
			if (this.tryEat(world, pos, state, player).isAccepted())
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
			player.addStat(Stats.EAT_CAKE_SLICE);
			player.getFoodStats().addStats(3, 0.3F);
			int i = state.get(BITES);
			if (i < 6)
				world.setBlockState(pos, state.with(BITES, Integer.valueOf(i + 1)), 3);
			else
				world.removeBlock(pos, false);
			return ActionResultType.SUCCESS;
		}
	}
}
