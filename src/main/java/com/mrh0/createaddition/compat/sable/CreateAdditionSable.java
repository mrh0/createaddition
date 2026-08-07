package com.mrh0.createaddition.compat.sable;

import net.neoforged.neoforge.common.NeoForge;

public class CreateAdditionSable {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(SableEvents::onPostPhysicsTick);
    }
}
