package com.mrh0.createaddition.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipeWrapper implements Container {

	public FluidStack fluid;
	
	public FluidRecipeWrapper(FluidStack fluid) {
		this.fluid = fluid;
	}
	
	@Override
	public void clearContent() {
		//fluid = new FluidStack(Fluids.EMPTY.getFluid(), 0);
	}

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int p_70301_1_) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_70304_1_) {
		return new ItemStack(Items.AIR);
	}

	@Override
	public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player p_70300_1_) {
		return true;
	}

}
