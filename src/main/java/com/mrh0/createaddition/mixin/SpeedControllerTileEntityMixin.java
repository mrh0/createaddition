package com.mrh0.createaddition.mixin;

import com.mrh0.createaddition.blocks.digital_adapter.ISpeedControllerAdapter;
import com.simibubi.create.content.contraptions.relays.advanced.SpeedControllerTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SpeedControllerTileEntity.class, remap = false)
public abstract class SpeedControllerTileEntityMixin implements ISpeedControllerAdapter {

    @Shadow
    protected ScrollValueBehaviour targetSpeed;

    public void setTargetSpeed(int speed) {
        this.targetSpeed.setValue(speed);
    }

    public int getTargetSpeed() {
        return this.targetSpeed.getValue();
    }
}
