package com.mrh0.createaddition.blocks.furnace_burner;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FurnaceBurnerTileEntity extends AbstractFurnaceTileEntity {

	public static final int FUEL_SLOT = 1;
	private static final int[] SLOTS = new int[] { FUEL_SLOT };

	public FurnaceBurnerTileEntity(TileEntityType<?> type) {
		super(type, IRecipeType.SMELTING);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("");
	}

	@Override
	protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
		return null;// new FurnaceContainer(p_213906_1_, p_213906_2_, this, this.furnaceData);
	}

	@Override
	protected boolean canBurn(IRecipe<?> p_214008_1_) {
		return true;
	}

	private boolean burning() {
		return this.litTime > 0;
	}

	public void tick() {
		boolean flag = this.burning();
		boolean flag1 = false;
		if (this.burning())
			--this.litTime;

		if (!this.level.isClientSide()) {
			ItemStack itemstack = this.items.get(1);
			if (!this.burning()) {
				this.litTime = this.getBurnDuration(itemstack);
				if (this.burning()) {
					flag1 = true;
					if (itemstack.hasContainerItem())
						this.items.set(1, itemstack.getContainerItem());
					else if (!itemstack.isEmpty()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty())
							this.items.set(1, itemstack.getContainerItem());
					}
				}
			}

			if (flag != this.burning()) {
				flag1 = true;
				this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT,
						Boolean.valueOf(this.burning())), 3);
			}

			if (flag1)
				this.setChanged();
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
		/*if (dir == Direction.DOWN && slot == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}*/
		return true;
	}
}
