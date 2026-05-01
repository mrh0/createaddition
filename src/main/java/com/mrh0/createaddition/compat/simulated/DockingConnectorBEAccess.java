package com.mrh0.createaddition.compat.simulated;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface DockingConnectorBEAccess {
    DockEnergyStorage getEnergyStorage();
    BlockPos getOtherConnectorPosition();
   // Level getLevel();
}
