package com.mrh0.createaddition.blocks.treated_gearbox;

import com.jozufozu.flywheel.backend.instancing.MaterialManager;
import com.simibubi.create.content.contraptions.base.HorizontalHalfShaftInstance;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class OppositeHorizontalShaftInstance extends HorizontalHalfShaftInstance {
    public OppositeHorizontalShaftInstance(MaterialManager<?> modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    }

    @Override
    protected Direction getShaftDirection() {
        return ((Direction)this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
