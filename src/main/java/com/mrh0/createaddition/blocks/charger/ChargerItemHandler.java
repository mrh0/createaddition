package com.mrh0.createaddition.blocks.charger;
/*
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ChargerItemHandler implements IItemHandler {

	private static final int MAIN_SLOT = 0;
	private ChargerBehaviour te;

	public ChargerItemHandler(ChargerBehaviour te) {
		this.te = te;
	}

	@Override
	public int getSlots() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == MAIN_SLOT ? te.getHeldItemStack() : te.processingOutputBuffer.getStackInSlot(slot - 1);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (slot != MAIN_SLOT)
			return stack;
		if (!te.getHeldItemStack()
			.isEmpty() && !te.canMergeItems())
			return stack;
		if (!te.isOutputEmpty() && !te.canMergeItems())
			return stack;

		ItemStack remainder = te.insert(new TransportedItemStack(stack), simulate);
		if (!simulate && remainder != stack)
			te.tileEntity.notifyUpdate();
		return remainder;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot != MAIN_SLOT)
			return te.processingOutputBuffer.extractItem(slot - 1, amount, simulate);

		TransportedItemStack held = te.heldItem;
		if (held == null)
			return ItemStack.EMPTY;
		ItemStack stack = held.stack.copy();
		ItemStack extracted = stack.split(amount);
		if (!simulate) {
			te.heldItem.stack = stack;
			if (stack.isEmpty())
				te.heldItem = null;
			te.tileEntity.notifyUpdate();
		}
		return extracted;
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot == MAIN_SLOT ? te.maxStackSize.get() : 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slot == MAIN_SLOT;
	}
}
*/