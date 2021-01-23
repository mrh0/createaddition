package com.mrh0.createaddition.blocks.rolling_mill;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class RollingMillBlock extends HorizontalKineticBlock implements ITE<RollingMillTileEntity> {

	public RollingMillBlock(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.ROLLING_MILL.create();
	}
	
	@Override
	public Class<RollingMillTileEntity> getTileEntityClass() {
		return RollingMillTileEntity.class;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AllShapes.MILLSTONE;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!player.getHeldItem(handIn).isEmpty())
			return ActionResultType.PASS;
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;

		withTileEntityDo(worldIn, pos, rollingMill -> {
			boolean emptyOutput = true;
			IItemHandlerModifiable inv = rollingMill.outputInv;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (!stackInSlot.isEmpty())
					emptyOutput = false;
				player.inventory.placeItemBackInInventory(worldIn, stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
			}

			if (emptyOutput) {
				inv = rollingMill.inputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					player.inventory.placeItemBackInInventory(worldIn, inv.getStackInSlot(slot));
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}
			}

			rollingMill.markDirty();
			rollingMill.sendData();
		});

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		super.onLanded(worldIn, entityIn);

		if (entityIn.world.isRemote)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		RollingMillTileEntity rollingMill = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.getPosition())) {
			try {
				rollingMill = getTileEntity(worldIn, pos);
			} catch (TileEntityException e) {
				continue;
			}
		}
		if (rollingMill == null)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		LazyOptional<IItemHandler> capability = rollingMill.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (!capability.isPresent())
			return;

		ItemStack remainder = capability.orElse(new ItemStackHandler()).insertItem(0, itemEntity.getItem(), false);
		if (remainder.isEmpty())
			itemEntity.remove();
		if (remainder.getCount() < itemEntity.getItem().getCount())
			itemEntity.setItem(remainder);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			withTileEntityDo(worldIn, pos, te -> {
				ItemHelper.dropContents(worldIn, pos, te.inputInv);
				ItemHelper.dropContents(worldIn, pos, te.outputInv);
			});

			worldIn.removeTileEntity(pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return getDefaultState().with(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.get(HORIZONTAL_FACING)
			.getAxis();
	}
}
