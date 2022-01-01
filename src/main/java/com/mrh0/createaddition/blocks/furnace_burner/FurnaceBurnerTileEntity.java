package com.mrh0.createaddition.blocks.furnace_burner;


import javax.annotation.Nullable;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBurnerTileEntity extends AbstractBurnerBlockEntity {

	public static final int FUEL_SLOT = 1;
	private static final int[] SLOTS = new int[] { FUEL_SLOT };
	static final int _litTime = 0, _litDuration = 1, _cookingProgress = 2, _cookingTotalTime = 3;

	public FurnaceBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, RecipeType.SMELTING);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("");
	}

	@Override
	protected AbstractContainerMenu createMenu(int p_213906_1_, Inventory p_213906_2_) {
		return null;// new FurnaceContainer(p_213906_1_, p_213906_2_, this, this.furnaceData);
	}

	@Override
	public boolean canBurn(@Nullable Recipe<?> p_155006_, NonNullList<ItemStack> p_155007_, int p_155008_) {
		return true;
	}
	
	public boolean isLit() {
		return this.dataAccess.get(_litTime) > 0;// this.litTime > 0;
	}

	public static void tick(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractBurnerBlockEntity be) {
		boolean flag = be.isLit();
		boolean flag1 = false;
		if (be.isLit())
			be.dataAccess.set(_litTime, be.dataAccess.get(_litTime));// --this.litTime;

		if (!be.getLevel().isClientSide()) {
			ItemStack itemstack = be.items.get(1);
			if (!be.isLit()) {
				be.dataAccess.set(_litTime, be.getBurnDuration(itemstack));// this.litTime =
																				// this.getBurnDuration(itemstack);
				if (be.isLit()) {
					flag1 = true;
					if (itemstack.hasContainerItem())
						be.items.set(1, itemstack.getContainerItem());
					else if (!itemstack.isEmpty()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty())
							be.items.set(1, itemstack.getContainerItem());
					}
				}
			}

			if (flag != be.isLit()) {
				flag1 = true;
				be.getLevel().setBlock(be.getBlockPos(), be.getLevel().getBlockState(be.getBlockPos())
						.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(be.isLit())), 3);
			}

			if (flag1)
				be.setChanged();
		}
	}

	public int[] getSlotsForFace(Direction dir) {
		return SLOTS;
	}

	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot != 1)
			return false;
		else {
			ItemStack itemstack = this.items.get(1);
			return isFuel(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
		}
	}

	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		/*
		 * if (dir == Direction.DOWN && slot == 1) { Item item = stack.getItem(); if
		 * (item != Items.WATER_BUCKET && item != Items.BUCKET) { return false; } }
		 */
		return true;
	}
}
