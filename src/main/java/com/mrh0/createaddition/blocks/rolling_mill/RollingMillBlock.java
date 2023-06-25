package com.mrh0.createaddition.blocks.rolling_mill;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Iterate;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
import org.jetbrains.annotations.NotNull;


@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class RollingMillBlock extends HorizontalKineticBlock implements IBE<RollingMillTileEntity> {

	public static final VoxelShape ROLLING_MILL_SHAPE = CAShapes.shape(0,0,0,16,5,16).add(2,0,2,14,16,14).build();
	
	public RollingMillBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.ROLLING_MILL.create(pos, state);
	}
	
	@Override
	public Class<RollingMillTileEntity> getBlockEntityClass() {
		return RollingMillTileEntity.class;
	}

	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return ROLLING_MILL_SHAPE;
	}
	
	@Override
	public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
		if (!player.getItemInHand(handIn).isEmpty())
			return InteractionResult.PASS;
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		withBlockEntityDo(worldIn, pos, rollingMill -> {
			boolean emptyOutput = true;
			ItemStackHandler inv = rollingMill.outputInv;
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
	public void updateEntityAfterFallOn(@NotNull BlockGetter worldIn, @NotNull Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);

		if (entityIn.level.isClientSide)
			return;
		if (!(entityIn instanceof ItemEntity itemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		RollingMillTileEntity rollingMill = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition())) {
			rollingMill = getBlockEntity(worldIn, pos);
		}
		if (rollingMill == null)
			return;

		Storage<ItemVariant> storage = TransferUtil.getItemStorage(rollingMill);
		if (storage == null)
			return;

		ItemStack remainder = itemEntity.getItem().copy();
		remainder.setCount((int) (remainder.getCount() - TransferUtil.insertItem(storage, itemEntity.getItem())));
		if (remainder.isEmpty())
			itemEntity.remove(RemovalReason.KILLED);
		if (remainder.getCount() < itemEntity.getItem().getCount())
			itemEntity.setItem(remainder);
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
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
		Direction preferredSide = getPreferredHorizontalFacing(context);
		if (preferredSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
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
	public BlockEntityType<? extends RollingMillTileEntity> getBlockEntityType() {
		return CATileEntities.ROLLING_MILL.get();
	}
}
