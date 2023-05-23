package com.mrh0.createaddition.blocks.rolling_mill;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

public class RollingMillInstance extends SingleRotatingInstance<RollingMillTileEntity> {
    public RollingMillInstance(MaterialManager dispatcher, RollingMillTileEntity tile) {
        super(dispatcher, tile);
    }

    @Override
    public void init() {
        super.init();
        //shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, ).createInstance();
        rotatingModel.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());
        transformModels();
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockEntity.getBlockState());
    }

    @Override
    public void update() {
        super.update();
        transformModels();
    }

    private void transformModels() {
        rotatingModel.setPosition(getInstancePosition())
                .nudge(0, 4f/16f, 0)
                .setRotationalSpeed(-getBlockEntitySpeed());
    }
    /*
    @Override
    public void remove() {
        super.remove();
        shaft.delete();
    }*/
}