package com.mrh0.createaddition.compat.computercraft;

import dan200.computercraft.api.ComputerCraftAPI;

public class ComputerCraftCompat {
    public static void registerCompat() {
        ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
    }
}