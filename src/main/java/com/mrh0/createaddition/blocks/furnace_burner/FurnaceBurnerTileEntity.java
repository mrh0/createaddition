package com.mrh0.createaddition.blocks.furnace_burner;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBurnerTileEntity extends AbstractFurnaceBlockEntity {

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

	/*@Override
	public boolean canBurn(@Nullable Recipe<?> p_155006_, NonNullList<ItemStack> p_155007_, int p_155008_) {
		return true;
	}*/

	private boolean burning() {
		return this.dataAccess.get(_litTime) > 0;//this.litTime > 0;
	}

	public void tick() {
		boolean flag = this.burning();
		boolean flag1 = false;
		if (this.burning())
			this.dataAccess.set(_litTime, this.dataAccess.get(_litTime));//--this.litTime;
		

		if (!this.level.isClientSide()) {
			ItemStack itemstack = this.items.get(1);
			if (!this.burning()) {
				this.dataAccess.set(_litTime, this.getBurnDuration(itemstack));//this.litTime = this.getBurnDuration(itemstack);
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
				this.level.setBlock(this.worldPosition, this.getLevel().getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT,
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
