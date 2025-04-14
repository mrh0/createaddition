package com.mrh0.createaddition.blocks.digital_adapter;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class DigitalAdapterBlockItem extends BlockItem {
    public DigitalAdapterBlockItem(Block block, Properties props) {
        super(block, props);
    }

    // TODO: Implement this again
    /*
    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (tab == CreativeModeTab.TAB_SEARCH) {
            super.fillItemCategory(tab, stacks);
        }
        if(tab == ModGroup.MAIN && CreateAddition.CC_ACTIVE) {
            super.fillItemCategory(tab, stacks);
        }
    }*/
}
