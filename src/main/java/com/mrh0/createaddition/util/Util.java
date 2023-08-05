package com.mrh0.createaddition.util;

import com.mrh0.createaddition.energy.WireType;
import com.mrh0.createaddition.item.WireSpool;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.energy.IEnergyStorage;

public class Util {
	public static int max(int...v) {
		int m = Integer.MIN_VALUE;
		for(int i : v)
			if(i > m)
				m = i;
		return m;
	}

	public static int min(int...v) {
		int m = Integer.MAX_VALUE;
		for(int i : v)
			if(i < m)
				m = i;
		return m;
	}

	public static int minIndex(int...v) {
		int m = 0;
		for(int i = 0; i < v.length; i++)
			if(v[i] < v[m])
				m = i;
		return m;
	}

	public static ItemStack findStack(Item item, Inventory inv) {
		for(int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if(stack.getItem() == item)
				return stack;
		}
		return ItemStack.EMPTY;
	}

	public static boolean canStack(ItemStack add, ItemStack to){
		return add.getCount() + to.getCount() <= to.getMaxStackSize() && (add.getItem() == to.getItem()) || to.isEmpty();
	}

	public static int getMergeRest(ItemStack add, ItemStack to){
		return Math.max(add.getCount() + to.getCount() - to.getMaxStackSize(), 0);
	}

	public static int getSkyLight(Level world, BlockPos pos) {
		return Math.max(world.getBrightness(LightLayer.SKY, pos) - world.getSkyDarken(), 0);
	}

	public static ItemStack mergeStack(ItemStack add, ItemStack to) {
		return new ItemStack(to.isEmpty() ? add.getItem() : to.getItem(), to.getCount() + add.getCount());
	}

	public static String format(int n) {
		if(n > 1000_000_000)
			return Math.round((double)n/100_000_000d)/10d + "G";
		if(n > 1000_000)
			return Math.round((double)n/100_000d)/10d + "M";
		if(n > 1000)
			return Math.round((double)n/100d)/10d + "K";
		return n + "";
	}

	public static MutableComponent getTextComponent(IEnergyStorage ies, String nan, String unit) {
		if(ies == null)
			return Component.literal(nan);
		return getTextComponent(ies.getEnergyStored(), unit).withStyle(ChatFormatting.AQUA).append(Component.literal(" / ").withStyle(ChatFormatting.GRAY)).append(getTextComponent(ies.getMaxEnergyStored(), unit));
	}

	public static MutableComponent getTextComponent(IEnergyStorage ies) {
		return getTextComponent(ies, "NaN", "fe");
	}

	public static MutableComponent getTextComponent(int value, String unit) {
		return Component.literal(format(value)+unit);
	}

	public static class Triple<A, B, C> {
		public final A a;
		public final B b;
		public final C c;
		private Triple(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
			return new Triple<A, B, C>(a, b, c);
		}
	}

	public static Util.Triple<BlockPos, Integer, WireType> getWireNodeOfSpools(ItemStack...stacks) {
		for(ItemStack stack : stacks) {
			if(stack.isEmpty()) continue;
			if(stack.getTag() == null) continue;
			if(WireSpool.hasPos(stack.getTag())) {
				return Util.Triple.of(WireSpool.getPos(stack.getTag()), WireSpool.getNode(stack.getTag()), WireType.of(stack.getItem()));
			}
		}
		return null;
	}
}
