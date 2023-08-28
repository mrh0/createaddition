package com.mrh0.createaddition.blocks.digital_adapter;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DigitalAdapterBlockItem extends BlockItem {
    public DigitalAdapterBlockItem(Block block, Properties props) {
        super(block, props);
    }

    /*public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (tab == CreativeModeTab.TAB_SEARCH) {
            super.fillItemCategory(tab, stacks);
        }
        if(tab == ModGroup.MAIN && CreateAddition.CC_ACTIVE) {
            super.fillItemCategory(tab, stacks);
        }
    }*/
}
