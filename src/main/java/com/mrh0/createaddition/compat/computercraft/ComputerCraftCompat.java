package com.mrh0.createaddition.compat.computercraft;

import dan200.computercraft.api.peripheral.PeripheralLookup;

public class ComputerCraftCompat {
    public static void registerCompat() {
        PeripheralLookup.get().registerFallback(PeripheralProvider::getPeripheral);
    }
}