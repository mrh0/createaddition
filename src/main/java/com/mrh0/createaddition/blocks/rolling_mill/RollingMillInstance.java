package com.mrh0.createaddition.blocks.rolling_mill;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;

public class RollingMillInstance extends ShaftInstance {

    private RotatingData shaft;

    public RollingMillInstance(MaterialManager dispatcher, RollingMillTileEntity tile) {
        super(dispatcher, tile);
    }

    @Override
    public void init() {
        super.init();
        shaft = getModel().createInstance();
        shaft.setRotationAxis(axis)
                .setRotationalSpeed(getTileSpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        transformModels();
    }

    @Override
    public void update() {
        super.update();
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