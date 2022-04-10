package com.mrh0.createaddition.blocks.base;

import javax.annotation.Nullable;

import com.mrh0.createaddition.transfer.SidedInvWrapper;
import com.simibubi.create.lib.transfer.fluid.FluidTransferable;
import com.simibubi.create.lib.transfer.fluid.IFluidHandler;
import com.simibubi.create.lib.transfer.item.IItemHandler;
import com.simibubi.create.lib.util.BurnUtil;
import com.simibubi.create.lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBurnerBlockEntity extends BlockEntity
		implements WorldlyContainer, FluidTransferable {
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

	public boolean isLit() {
		return this.litTime > 0;
	}

	public void load(CompoundTag nbt) {
		if(nbt == null)
			nbt = new CompoundTag();
		super.load(nbt);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items);
		this.litTime = nbt.getInt("BurnTime");
		this.litDuration = this.getBurnDuration(this.items.get(0));
	}

	protected void saveAdditional(CompoundTag p_187452_) {
		super.saveAdditional(p_187452_);
		p_187452_.putInt("BurnTime", this.litTime);
		ContainerHelper.saveAllItems(p_187452_, this.items);
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
					if (itemstack.getItem().hasCraftingRemainingItem())
						be.items.set(0, new ItemStack(itemstack.getItem().getCraftingRemainingItem()));
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
		if (stack.isEmpty()) {
			return 0;
		} else {
			return BurnUtil.getBurnTime(stack);
		}
	}
	
	public static boolean isFuel(ItemStack stack) {
		return BurnUtil.getBurnTime(stack) > 0;
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
		return BurnUtil.getBurnTime(stack) > 0
				|| stack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
	}

	public void clearContent() {
		this.items.clear();
	}

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper
			.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@org.jetbrains.annotations.Nullable
	@Override
	public IFluidHandler getFluidHandler(@org.jetbrains.annotations.Nullable Direction facing) {
		if (!this.remove && facing != null) {
			return (IFluidHandler) handlers[0].cast().getValueUnsafer();
		}
		return null;
	}

//	@Override
//	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(
//			net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
//		if (!this.remove && facing != null
//				&& capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//			return handlers[0].cast();
//		}
//		return super.getCapability(capability, facing);
//	}

//	@Override
	public void invalidateCaps() {
//		super.invalidateCaps();
		for (int x = 0; x < handlers.length; x++)
			handlers[x].invalidate();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		invalidateCaps();
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}
}
