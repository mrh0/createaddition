package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayer;

import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;

public class LiquidBlazeBurnerBlock extends HorizontalDirectionalBlock implements IBE<LiquidBlazeBurnerBlockEntity>, IWrenchable {

	public LiquidBlazeBurnerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE));
	}

	public static final MapCodec<LiquidBlazeBurnerBlock> CODEC = simpleCodec(LiquidBlazeBurnerBlock::new);

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	public static BlazeBurnerBlock.HeatLevel getHeatLevelOf(BlockState blockState) {
		return blockState.hasProperty(HEAT_LEVEL) ? blockState.getValue(HEAT_LEVEL)
				: BlazeBurnerBlock.HeatLevel.NONE;
	}

	public static int getLight(BlockState state) {
		BlazeBurnerBlock.HeatLevel level = state.getValue(HEAT_LEVEL);
		return switch (level) {
			case NONE -> 0;
			case SMOULDERING -> 8;
			default -> 15;
		};
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HEAT_LEVEL, FACING);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
		if (world.isClientSide) return;
		BlockEntity tileEntity = world.getBlockEntity(pos.above());
		if (!(tileEntity instanceof BasinBlockEntity basin)) return;
        basin.notifyChangeOfContents();
	}

	@Override
	public Class<LiquidBlazeBurnerBlockEntity> getBlockEntityClass() {
		return LiquidBlazeBurnerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LiquidBlazeBurnerBlockEntity> getBlockEntityType() {
		return CABlockEntities.LIQUID_BLAZE_BURNER.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IBE.super.newBlockEntity(pos, state);
	}

	@Override
	public Item asItem() {
		return AllBlocks.BLAZE_BURNER.get().asItem();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack heldItem = player.getItemInHand(hand);
		BlazeBurnerBlock.HeatLevel heat = state.getValue(HEAT_LEVEL);

		if (AllItems.GOGGLES.isIn(heldItem) && heat != BlazeBurnerBlock.HeatLevel.NONE)
			return onBlockEntityUseItemOn(level, pos, be -> {
				if (be.goggles) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				be.goggles = true;
				be.notifyUpdate();
				return ItemInteractionResult.SUCCESS;
			});

		if (heldItem.isEmpty() && heat != BlazeBurnerBlock.HeatLevel.NONE)
			return onBlockEntityUseItemOn(level, pos, be -> {
				if (!be.goggles) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				be.goggles = false;
				be.notifyUpdate();
				return ItemInteractionResult.SUCCESS;
			});

		boolean doNotConsume = player.isCreative();
		boolean forceOverflow = !(player instanceof FakePlayer);

		InteractionResultHolder<ItemStack> res =
			tryInsert(state, level, pos, heldItem, doNotConsume, forceOverflow, false);
		ItemStack leftover = res.getObject();
		if (!level.isClientSide && !doNotConsume && !leftover.isEmpty()) {
			if (heldItem.isEmpty()) {
				player.setItemInHand(hand, leftover);
			} else if (!player.getInventory()
				.add(leftover)) {
				player.drop(leftover, false);
			}
		}

		return res.getResult() == InteractionResult.SUCCESS ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level level, BlockPos pos,
		ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
		if (!state.hasBlockEntity()) return InteractionResultHolder.fail(ItemStack.EMPTY);

		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof LiquidBlazeBurnerBlockEntity burnerTE))
			return InteractionResultHolder.fail(ItemStack.EMPTY);

        if (burnerTE.isCreativeFuel(stack)) {
			if (!simulate) burnerTE.applyCreativeFuel();
			return InteractionResultHolder.success(ItemStack.EMPTY);
		}
		if (!burnerTE.tryUpdateFuel(stack, forceOverflow, simulate)) return InteractionResultHolder.fail(ItemStack.EMPTY);

		if (!doNotConsume) {
			ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
			if (!level.isClientSide) {stack.shrink(1);
			}
			return InteractionResultHolder.success(container);
		}
		return InteractionResultHolder.success(ItemStack.EMPTY);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return AllShapes.HEATER_BLOCK_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos,
		CollisionContext context) {
		if (context == CollisionContext.empty()) return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
		return getShape(state, getter, pos, context);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return Math.max(0, state.getValue(HEAT_LEVEL)
			.ordinal() - 1);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		if (random.nextInt(10) != 0) return;
		if (!state.getValue(HEAT_LEVEL).isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING)) return;
		world.playLocalSound((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F,
                (float) pos.getZ() + 0.5F, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
			0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
	}
}
