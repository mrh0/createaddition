package com.mrh0.createaddition.blocks.digital_adapter;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class DigitalAdapterDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        if(context.getSourceBlockEntity() == null) return List.of();
        if(context.getSourceBlockEntity() instanceof DigitalAdapterBlockEntity date)
            return date.textLines;
        return List.of();
    }
}
