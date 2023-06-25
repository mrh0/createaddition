package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings({"deprecation"})
public class LiquidBlazeBurner extends HorizontalDirectionalBlock implements IBE<LiquidBlazeBurnerTileEntity>, IWrenchable {

	public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", HeatLevel.class);

	public LiquidBlazeBurner(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, HeatLevel.NONE));
	}

	@Override
	protected void createBlockStateDefinition(@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HEAT_LEVEL, FACING);
	}
	public static InteractionResultHolder<ItemStack> tryInsert(
			BlockState state,
			Level world,
			BlockPos pos,
			ItemStack stack,
			boolean doNotConsume,
			boolean forceOverflow,
			boolean simulate,
			Player player,
			InteractionHand hand
	) {
		if (!state.hasBlockEntity())
			return InteractionResultHolder.fail(ItemStack.EMPTY);

		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof LiquidBlazeBurnerTileEntity burnerTE))
			return InteractionResultHolder.fail(ItemStack.EMPTY);

		if (!burnerTE.tryUpdateFuel(stack, forceOverflow, simulate, player, hand))
			return InteractionResultHolder.fail(ItemStack.EMPTY);

		if (!doNotConsume) {
			ItemStack container;
			if (stack.getItem().hasCraftingRemainingItem()) {
				assert stack.getItem().getCraftingRemainingItem() != null;
				container = stack.getItem().getCraftingRemainingItem().getDefaultInstance();
			} else {
				container = ItemStack.EMPTY;
			}
			if (!world.isClientSide) {
				stack.shrink(1);
			}
			return InteractionResultHolder.success(container);
		}
		return InteractionResultHolder.success(ItemStack.EMPTY);
	}

	@Override
	public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState p_220082_4_, boolean p_220082_5_) {
		if (world.isClientSide)
			return;
		BlockEntity tileEntity = world.getBlockEntity(pos.above());
		if (!(tileEntity instanceof BasinBlockEntity basin))
			return;
		basin.notifyChangeOfContents();
	}
	@Override
	public @NotNull InteractionResult use(BlockState state, @NotNull Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand,
										  @NotNull BlockHitResult blockRayTraceResult) {

		ItemStack heldItem = player.getItemInHand(hand);
		HeatLevel heat = state.getValue(HEAT_LEVEL);

		if (AllItems.GOGGLES.isIn(heldItem) && heat != HeatLevel.NONE)
			return onBlockEntityUse(world, pos, bbte -> {
				if (bbte.goggles)
					return InteractionResult.PASS;
				bbte.goggles = true;
				bbte.notifyUpdate();
				return InteractionResult.SUCCESS;
			});

		if (heldItem.isEmpty() && heat != HeatLevel.NONE)
			return onBlockEntityUse(world, pos, bbte -> {
				if (!bbte.goggles)
					return InteractionResult.PASS;
				bbte.goggles = false;
				bbte.notifyUpdate();
				return InteractionResult.SUCCESS;
			});

		boolean doNotConsume = player.isCreative();
		boolean forceOverflow = !(player instanceof FakePlayer);
		InteractionResultHolder<ItemStack> res =
				tryInsert(state, world, pos, heldItem, doNotConsume, forceOverflow, false, player, hand);
		ItemStack leftover = res.getObject();
		if (!world.isClientSide && !doNotConsume && !leftover.isEmpty()) {
			if (heldItem.isEmpty()) {
				player.setItemInHand(hand, leftover);
			} else if (!player.getInventory()
					.add(leftover)) {
				player.drop(leftover, false);
			}
		}

		return res.getResult() == InteractionResult.SUCCESS ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}
	@Override
	public void fillItemCategory(@NotNull CreativeModeTab group, NonNullList<ItemStack> list) {
		list.add(AllItems.EMPTY_BLAZE_BURNER.asStack());
		super.fillItemCategory(group, list);
	}

	@Override
	public Class<LiquidBlazeBurnerTileEntity> getBlockEntityClass() {
		return LiquidBlazeBurnerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends LiquidBlazeBurnerTileEntity> getBlockEntityType() {
		return CATileEntities.LIQUID_BLAZE_BURNER.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IBE.super.newBlockEntity(pos, state);
	}
	
	@Override
	public @NotNull Item asItem() {
		return AllBlocks.BLAZE_BURNER.get().asItem();
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return AllShapes.HEATER_BLOCK_SHAPE;
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState p_220071_1_, @NotNull BlockGetter p_220071_2_, @NotNull BlockPos p_220071_3_,
												 @NotNull CollisionContext p_220071_4_) {
		if (p_220071_4_ == CollisionContext.empty())
			return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
		return getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
	}

	@Override
	public boolean hasAnalogOutputSignal(@NotNull BlockState p_149740_1_) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, @NotNull Level p_180641_2_, @NotNull BlockPos p_180641_3_) {
		return Math.max(0, state.getValue(HEAT_LEVEL)
			.ordinal() - 1);
	}

	@Override
	public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull PathComputationType type) {
		return false;
	}

}
