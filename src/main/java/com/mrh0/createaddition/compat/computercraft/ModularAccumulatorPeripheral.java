package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.mrh0.createaddition.config.Config;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModularAccumulatorPeripheral implements IPeripheral {
    private final String type;
    private final ModularAccumulatorBlockEntity tileEntity;

    public ModularAccumulatorPeripheral(String type, ModularAccumulatorBlockEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return this.tileEntity;
    }

    @LuaFunction(mainThread = true)
    public final int getEnergy() {
        if(this.tileEntity.getControllerBE() == null) return 0;
        return this.tileEntity.getControllerBE().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getCapacity() {
        if(this.tileEntity.getControllerBE() == null) return 0;
        return this.tileEntity.getControllerBE().getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final float getPercent() {
        if(this.tileEntity.getControllerBE() == null) return 0;
        return this.tileEntity.getControllerBE().getPercent();
    }

    @LuaFunction(mainThread = true)
    public int getMaxInsert() {
        return Config.ACCUMULATOR_MAX_INPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getMaxExtract() {
        return Config.ACCUMULATOR_MAX_OUTPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getHeight() {
        return this.tileEntity.getHeight();
    }

    @LuaFunction(mainThread = true)
    public int getWidth() {
        return this.tileEntity.getWidth();
    }
}
