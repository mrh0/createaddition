package com.mrh0.createaddition.blocks.base;

import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.network.IObserveTileEntity;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractBurnerBlockEntity extends SmartTileEntity implements IObserveTileEntity, WorldlyContainer, ItemTransferable {
	public static final int SLOT_FUEL = 0;
	public static final int DATA_LIT_TIME = 0;
	private static final int[] SLOTS = new int[] { 0 };
	public static final int DATA_LIT_DURATION = 1;
	public static final int NUM_DATA_VALUES = 4;
	public static final int BURN_TIME_STANDARD = 200;
	public static final int BURN_COOL_SPEED = 2;
	public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
	public int litTime;
	public int litDuration;

	protected AbstractBurnerBlockEntity(BlockEntityType<?> p_154991_, BlockPos p_154992_, BlockState p_154993_) {
		super(p_154991_, p_154992_, p_154993_);
	}


	@Override
	public void addBehaviours(List<TileEntityBehaviour> arg0) {
	}

	public boolean isLit() {
		return this.litTime > 0;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		if(nbt == null)
			nbt = new CompoundTag();
		super.read(nbt, clientPacket);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
		this.litTime = nbt.getInt("BurnTime");
		this.litDuration = this.getBurnDuration(this.items.get(0));
	}

	@Override
	protected void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		nbt.putInt("BurnTime", this.litTime);
		ContainerHelper.saveAllItems(nbt, this.items);
	}

	public static void serverTick(Level world, BlockPos pos, BlockState state,
			AbstractBurnerBlockEntity be) {
		boolean flag = be.isLit();
		boolean flag1 = false;
		if (be.isLit()) {
			--be.litTime;
		}

		ItemStack itemstack = be.items.get(0);
		if (be.isLit() || !itemstack.isEmpty()) {
			if (!be.isLit()) {
				be.litTime = be.getBurnDuration(itemstack);
				be.litDuration = be.litTime;
				if (be.isLit()) {
					flag1 = true;
					if (itemstack.getItem().hasCraftingRemainingItem()) {
						be.items.set(0, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
					}
					else if (!itemstack.isEmpty()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty()) {
							be.items.set(0, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
						}
					}
				}
			}
		}

		if (flag != be.isLit()) {
			flag1 = true;
			state = state.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(be.isLit()));
			world.setBlock(pos, state, 3);
		}

		if (flag1) {
			setChanged(world, pos, state);
		}

	}

	public int getBurnDuration(ItemStack stack) {
		if (stack.isEmpty() || FuelRegistry.INSTANCE.get(stack.getItem()) == null) {
			return 0;
		} else {
			return FuelRegistry.INSTANCE.get(stack.getItem());
		}
	}
	
	public static boolean isFuel(ItemStack stack) {
		return FuelRegistry.INSTANCE.get(stack.getItem()) > 0;
	}

	public int[] getSlotsForFace(Direction dir) {
		return SLOTS;
	}

	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
		return this.canPlaceItem(index, stack);
	}

	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
		return true;
	}

	public int getContainerSize() {
		return this.items.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	public ItemStack removeItem(int index, int p_58331_) {
		return ContainerHelper.removeItem(this.items, index, p_58331_);
	}

	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	public void setItem(int index, ItemStack stack) {
		this.items.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
	}

	public boolean canPlaceItem(int index, ItemStack stack) {
		ItemStack itemstack = this.items.get(0);
		return FuelRegistry.INSTANCE.get(stack.getItem()) != null
				|| stack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
	}

	public void clearContent() {
		this.items.clear();
	}

	InventoryStorage[] handlers = new InventoryStorage[] {
			InventoryStorage.of(this, Direction.UP),
			InventoryStorage.of(this, Direction.DOWN),
			InventoryStorage.of(this, Direction.NORTH)
	};

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction facing) {
		if (!this.remove && facing != null) {
			return handlers[0];
		}
		return null;
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}
}
