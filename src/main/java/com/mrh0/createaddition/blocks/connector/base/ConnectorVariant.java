package com.mrh0.createaddition.blocks.connector.base;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public enum ConnectorVariant implements StringRepresentable {
    Default("default"),
    Girder("girder");

    private String name;
    ConnectorVariant(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static ConnectorVariant test(Level level, BlockPos pos, Direction face) {
        BlockState state = level.getBlockState(pos);
        if(state.is(AllBlocks.METAL_GIRDER.get())) {
            if(state.getValue(GirderBlock.TOP) && face == Direction.UP) return Default;
            if(state.getValue(GirderBlock.BOTTOM) && face == Direction.DOWN) return Default;
            if(state.getValue(GirderBlock.X) && face.getAxis() == Direction.Axis.X) return Default;
            if(state.getValue(GirderBlock.Z) && face.getAxis() == Direction.Axis.Z) return Default;
            return Girder;
        }
        if(state.is(AllBlocks.METAL_GIRDER_ENCASED_SHAFT.get())){
            if(!state.getValue(GirderEncasedShaftBlock.TOP) && face == Direction.UP) return Girder;
            if(!state.getValue(GirderEncasedShaftBlock.BOTTOM) && face == Direction.DOWN) return Girder;
            return Default;
        }
        return Default;
    }
}