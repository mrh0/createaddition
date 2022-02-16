package com.mrh0.createaddition.transfer;

import net.minecraft.core.Direction;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

public interface EnergyTransferable {
    @Nullable
    EnergyStorage getEnergyStorage(@Nullable Direction direction);
}
