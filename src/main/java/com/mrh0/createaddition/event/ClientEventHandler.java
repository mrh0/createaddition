package com.mrh0.createaddition.event;

import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.mrh0.createaddition.util.Util;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

public class ClientEventHandler {
    public static boolean clientRenderHeldWire;

    public static void playerRendererEvent(ClientLevel level) {
        if(ClientMinecraftWrapper.getPlayer() == null) return;
        ItemStack stack = ClientMinecraftWrapper.getPlayer().getInventory().getSelected();
        if(stack.isEmpty()) return;
        if(WireSpool.isRemover(stack.getItem())) return;
        clientRenderHeldWire = Util.getWireNodeOfSpools(stack) != null;
        CASoundScapes.tick();
    }
}