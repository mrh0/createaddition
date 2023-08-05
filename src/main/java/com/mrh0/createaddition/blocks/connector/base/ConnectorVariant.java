package com.mrh0.createaddition.blocks.connector.base;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public enum ConnectorVariant implements StringRepresentable {
    Default("default"),
    Girder("Girder");

    private String name;
    ConnectorVariant(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static ConnectorVariant test(Level level, BlockPos pos, Direction face) {
        BlockState state = level.getBlockState(pos.relative(face));
        if(state.is(AllBlocks.METAL_GIRDER.get()) || state.is(AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get())) {
            return Girder;
        }
        return Default;
    }
}