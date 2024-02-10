package com.mrh0.createaddition.event;

import com.mrh0.createaddition.sound.CASoundScapes;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadListener implements ResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CASoundScapes.invalidateAll();
    }
}
