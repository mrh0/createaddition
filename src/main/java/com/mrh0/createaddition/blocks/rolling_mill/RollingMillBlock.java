package com.mrh0.createaddition.blocks.rolling_mill;

import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class RollingMillBlock extends HorizontalKineticBlock implements IBE<RollingMillBlockEntity> {

	public static final VoxelShape ROLLING_MILL_SHAPE = CAShapes.shape(0,0,0,16,5,16).add(2,0,2,14,16,14).build();
	
	public RollingMillBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ROLLING_MILL_SHAPE;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (!player.getItemInHand(handIn).isEmpty())
			return InteractionResult.PASS;
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		withBlockEntityDo(worldIn, pos, rollingMill -> {
			boolean emptyOutput = true;
			IItemHandlerModifiable inv = rollingMill.outputInv;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (!stackInSlot.isEmpty())
					emptyOutput = false;
				player.getInventory().placeItemBackInInventory(stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
			}

			if (emptyOutput) {
				inv = rollingMill.inputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					player.getInventory().placeItemBackInInventory(inv.getStackInSlot(slot));
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}
			}

			rollingMill.setChanged();
			rollingMill.sendData();
		});

		return InteractionResult.SUCCESS;
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);

		if (entityIn.level().isClientSide)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		RollingMillBlockEntity rollingMill = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition())) {
			rollingMill = getBlockEntity(worldIn, pos);
		}
		if (rollingMill == null)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		LazyOptional<IItemHandler> capability = rollingMill.getCapability(ForgeCapabilities.ITEM_HANDLER);
		if (!capability.isPresent())
			return;

		ItemStack remainder = capability.orElse(new ItemStackHandler()).insertItem(0, itemEntity.getItem(), false);
		if (remainder.isEmpty())
			itemEntity.remove(RemovalReason.KILLED);
		if (remainder.getCount() < itemEntity.getItem().getCount())
			itemEntity.setItem(remainder);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
			withBlockEntityDo(worldIn, pos, te -> {
				ItemHelper.dropContents(worldIn, pos, te.inputInv);
				ItemHelper.dropContents(worldIn, pos, te.outputInv);
			});

			worldIn.removeBlockEntity(pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public BlockEntityType<? extends RollingMillBlockEntity> getBlockEntityType() {
		return CABlockEntities.ROLLING_MILL.get();
	}

	@Override
	public Class<RollingMillBlockEntity> getBlockEntityClass() {
		return RollingMillBlockEntity.class;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.ROLLING_MILL.create(pos, state);
	}
}
