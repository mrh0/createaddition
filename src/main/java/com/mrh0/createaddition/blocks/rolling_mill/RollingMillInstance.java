package com.mrh0.createaddition.blocks.rolling_mill;

import com.jozufozu.flywheel.backend.instancing.IDynamicInstance;
import com.jozufozu.flywheel.backend.material.MaterialManager;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;

public class RollingMillInstance extends ShaftInstance implements IDynamicInstance {

    private final RotatingData shaft;

    public RollingMillInstance(MaterialManager<?> dispatcher, RollingMillTileEntity tile) {
        super(dispatcher, tile);

        shaft = getRotatingMaterial()
                .getModel(AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, blockState.getValue(RollingMillBlock.HORIZONTAL_FACING).getAxis()))
                .createInstance();

        //Quaternion q = Vector3f.POSITIVE_Y.getDegreesQuaternion(AngleHelper.horizontalAngle(blockState.get(RollingMillBlock.HORIZONTAL_FACING)));

        //shaft.setRotation(q);
        shaft.setRotationAxis(blockState.getValue(RollingMillBlock.HORIZONTAL_FACING).getAxis());

        transformModels();
    }

    @Override
    public void beginFrame() {
        transformModels();
    }

    private void transformModels() {
        shaft.setPosition(getInstancePosition())
                .nudge(0, 4f/16f, 0)
                .setRotationalSpeed(-getTileSpeed());
    }

    @Override
    public void updateLight() {
        super.updateLight();
        
        relight(pos, shaft);
    }

    @Override
    public void remove() {
        super.remove();
        shaft.delete();
    }
}
