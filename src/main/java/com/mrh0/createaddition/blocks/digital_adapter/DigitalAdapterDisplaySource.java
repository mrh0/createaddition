package com.mrh0.createaddition.blocks.digital_adapter;

import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class DigitalAdapterDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if(context.getSourceTE() == null) return List.of();
        if(context.getSourceTE() instanceof DigitalAdapterTileEntity date)
            return date.textLines;
        return List.of();
    }
}
