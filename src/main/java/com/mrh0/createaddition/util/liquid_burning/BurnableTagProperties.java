package com.mrh0.createaddition.util.liquid_burning;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.resources.ResourceLocation;


public class BurnableTagProperties {
    private final boolean superheated;
    private final int time;
    private final ResourceLocation tagResource;

    public BurnableTagProperties(boolean superheated, int timePerBucket, boolean crashOnLow) {
        if (superheated) {
            this.tagResource = new ResourceLocation(CreateAddition.MODID, "burnable_fuel_superheated_" + timePerBucket);
        } else {
            this.tagResource = new ResourceLocation(CreateAddition.MODID, "burnable_fuel_" + timePerBucket);
        }
        if (timePerBucket < 200 && crashOnLow) {
            throw new RuntimeException("Burning Time cannot be less then 200! Change this to stop this crash " +
                    "\n[Conflicting Tag ---> " + tagResource + "]\n");
        }

        int i = timePerBucket / 100;

        if (i * 100 != timePerBucket ) {
            throw new RuntimeException("Burning Time is indivisible by 100! Change this to stop this crash " +
                    "\n[Conflicting Tag ---> " + tagResource + "]\n");
        }

        this.superheated = superheated;
        this.time = timePerBucket;
    }

    @SuppressWarnings("unused")
    public BurnableTagProperties(boolean superheated, int timePerBucket) {
        this(superheated, timePerBucket, true);
    }

    public boolean getSuperheated() {
        return this.superheated;
    }

    public int getTime() {
        return this.time;
    }

    public int getDropletAmount() {
        return 81000;
    }
    public ResourceLocation asResource() {
        return this.tagResource;
    }
}
