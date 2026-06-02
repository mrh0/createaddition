package com.mrh0.createaddition.item;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.util.Util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CapacitorItem extends Item {
    private static final String ENERGY_KEY = "energy";

    public CapacitorItem(Properties props) {
        super(props.stacksTo(16));
    }

    public static int getEnergy(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyTag().getInt(ENERGY_KEY);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        if (energy <= 0) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.remove(ENERGY_KEY);
            if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
            else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        } else {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt(ENERGY_KEY, Math.min(energy, CommonConfig.CAPACITOR_CAPACITY.get()));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        IEnergyStorage es = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (es != null) tooltip.add(Util.getTextComponent(es));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getEnergy(stack) > 0 ? 1 : 16;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getEnergy(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float) getEnergy(stack) / CommonConfig.CAPACITOR_CAPACITY.get() * 13f);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00BFFF;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
            Capabilities.EnergyStorage.ITEM,
            (stack, ctx) -> new CapacitorEnergyStorage(stack),
            CAItems.CAPACITOR.get()
        );
    }

    public static class CapacitorEnergyStorage implements IEnergyStorage {
        private final ItemStack stack;

        public CapacitorEnergyStorage(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int capacity = CommonConfig.CAPACITOR_CAPACITY.get();
            int stored = getEnergy(stack);
            int accepted = Math.min(maxReceive, Math.min(CommonConfig.CAPACITOR_CHARGE_RATE.get(), capacity - stored));
            if (accepted <= 0) return 0;
            if (!simulate) setEnergy(stack, stored + accepted);
            return accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int stored = getEnergy(stack);
            int extracted = Math.min(maxExtract, Math.min(CommonConfig.CAPACITOR_CHARGE_RATE.get(), stored));
            if (extracted <= 0) return 0;
            if (!simulate) setEnergy(stack, stored - extracted);
            return extracted;
        }

        @Override public int getEnergyStored() { return getEnergy(stack); }
        @Override public int getMaxEnergyStored() { return CommonConfig.CAPACITOR_CAPACITY.get(); }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return true; }
    }
}
