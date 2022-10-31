package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class LiquidBlazeBurner extends HorizontalDirectionalBlock implements ITE<LiquidBlazeBurnerTileEntity>, IWrenchable {

	public static final EnumProperty<HeatLevel> HEAT_LEVEL = EnumProperty.create("blaze", HeatLevel.class);

	public LiquidBlazeBurner(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, HeatLevel.NONE));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HEAT_LEVEL, FACING);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
		if (world.isClientSide)
			return;
		BlockEntity tileEntity = world.getBlockEntity(pos.above());
		if (!(tileEntity instanceof BasinTileEntity))
			return;
		BasinTileEntity basin = (BasinTileEntity) tileEntity;
		basin.notifyChangeOfContents();
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
		list.add(AllItems.EMPTY_BLAZE_BURNER.asStack());
		super.fillItemCategory(group, list);
	}

	@Override
	public Class<LiquidBlazeBurnerTileEntity> getTileEntityClass() {
		return LiquidBlazeBurnerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends LiquidBlazeBurnerTileEntity> getTileEntityType() {
		return CATileEntities.LIQUID_BLAZE_BURNER.get();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ITE.super.newBlockEntity(pos, state);
	}
	
	@Override
	public Item asItem() {
		return AllBlocks.BLAZE_BURNER.get().asItem();
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
		BlockHitResult blockRayTraceResult) {
		
		
		/*if (world.isClientSide())
			return InteractionResult.CONSUME;
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof LiquidBlazeBurnerTileEntity) {
			LiquidBlazeBurnerTileEntity cbte = (LiquidBlazeBurnerTileEntity) tileentity;
			ItemStack held = player.getMainHandItem();
			if (!(held.getItem() instanceof BucketItem))
				return InteractionResult.SUCCESS;
			LazyOptional<IFluidHandlerItem> cap = held
					.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
			if (!cap.isPresent())
				return InteractionResult.SUCCESS;
			IFluidHandlerItem handler = cap.orElse(null);
			if (handler.getFluidInTank(0).isEmpty())
				return InteractionResult.CONSUME;
			FluidStack stack = handler.getFluidInTank(0);
			Optional<LiquidBurningRecipe> recipe = cbte.find(stack, world);
			if (!recipe.isPresent())
				return InteractionResult.SUCCESS;

			LazyOptional<IFluidHandler> tecap = cbte.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
			if (!tecap.isPresent())
				return InteractionResult.SUCCESS;
			IFluidHandler tehandler = tecap.orElse(null);
			if (tehandler.getTankCapacity(0) - tehandler.getFluidInTank(0).getAmount() < 1000)
				return InteractionResult.SUCCESS;
			tehandler.fill(new FluidStack(handler.getFluidInTank(0).getFluid(), 1000), FluidAction.EXECUTE);
			if (!player.isCreative())
				player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
			player.playSound(SoundEvents.BUCKET_EMPTY, 1f, 1f);
		}
		return InteractionResult.PASS;*/
		
		
		
		ItemStack heldItem = player.getItemInHand(hand);
		HeatLevel heat = state.getValue(HEAT_LEVEL);

		if (AllItems.GOGGLES.isIn(heldItem) && heat != HeatLevel.NONE)
			return onTileEntityUse(world, pos, bbte -> {
				if (bbte.goggles)
					return InteractionResult.PASS;
				bbte.goggles = true;
				bbte.notifyUpdate();
				return InteractionResult.SUCCESS;
			});

		if (heldItem.isEmpty() && heat != HeatLevel.NONE)
			return onTileEntityUse(world, pos, bbte -> {
				if (!bbte.goggles)
					return InteractionResult.PASS;
				bbte.goggles = false;
				bbte.notifyUpdate();
				return InteractionResult.SUCCESS;
			});

		if (heat == HeatLevel.NONE) {
			if (heldItem.getItem() instanceof FlintAndSteelItem) {
				world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
					world.random.nextFloat() * 0.4F + 0.8F);
				if (world.isClientSide)
					return InteractionResult.SUCCESS;
				heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				world.setBlockAndUpdate(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}

		boolean doNotConsume = player.isCreative();
		boolean forceOverflow = !(player instanceof FakePlayer);

		InteractionResultHolder<ItemStack> res =
			tryInsert(state, world, pos, heldItem, doNotConsume, forceOverflow, false);
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

	public static InteractionResultHolder<ItemStack> tryInsert(BlockState state, Level world, BlockPos pos,
		ItemStack stack, boolean doNotConsume, boolean forceOverflow, boolean simulate) {
		if (!state.hasBlockEntity())
			return InteractionResultHolder.fail(ItemStack.EMPTY);

		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof LiquidBlazeBurnerTileEntity))
			return InteractionResultHolder.fail(ItemStack.EMPTY);
		LiquidBlazeBurnerTileEntity burnerTE = (LiquidBlazeBurnerTileEntity) te;

		if (burnerTE.isCreativeFuel(stack)) {
			if (!simulate)
				burnerTE.applyCreativeFuel();
			return InteractionResultHolder.success(ItemStack.EMPTY);
		}
		if (!burnerTE.tryUpdateFuel(stack, forceOverflow, simulate))
			return InteractionResultHolder.fail(ItemStack.EMPTY);

		if (!doNotConsume) {
			ItemStack container = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
			if (!world.isClientSide) {
				stack.shrink(1);
			}
			return InteractionResultHolder.success(container);
		}
		return InteractionResultHolder.success(ItemStack.EMPTY);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return AllShapes.HEATER_BLOCK_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_,
		CollisionContext p_220071_4_) {
		if (p_220071_4_ == CollisionContext.empty())
			return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
		return getShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
		return Math.max(0, state.getValue(HEAT_LEVEL)
			.ordinal() - 1);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		if (random.nextInt(10) != 0)
			return;
		if (!state.getValue(HEAT_LEVEL)
			.isAtLeast(HeatLevel.SMOULDERING))
			return;
		world.playLocalSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
			(double) ((float) pos.getZ() + 0.5F), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
			0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
	}
}
