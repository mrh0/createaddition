package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterTileEntity;
import com.simibubi.create.content.contraptions.relays.gauge.StressGaugeTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DigitalAdapterPeripheral implements IPeripheral {
    private final String type;
    private final DigitalAdapterTileEntity tileEntity;

    public DigitalAdapterPeripheral(String type, DigitalAdapterTileEntity tileEntity) {
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

    // Display Link

    @LuaFunction(mainThread = true)
    public final void clearLine() {
        this.tileEntity.clearLine(this.tileEntity.getLine());
    }

    @LuaFunction(mainThread = true)
    public final void clear() {
        this.tileEntity.clearAll();
    }

    @LuaFunction(mainThread = true)
    public final void print(String text) {
        this.tileEntity.setTextLine(this.tileEntity.getLine(), new TextComponent(text.substring(0, 128)));
        this.tileEntity.incrementLine();
    }

    //@LuaFunction(mainThread = true)
    //public final void write(String text) {
    //    this.tileEntity.append(this.tileEntity.getLine(), new TextComponent(text));
    //}

    @LuaFunction(mainThread = true)
    public final int getLine() {
        return this.tileEntity.getLine();
    }

    @LuaFunction(mainThread = true)
    public final int setLine(int ln) {
        return this.tileEntity.setLine(ln);
    }

    @LuaFunction(mainThread = true)
    public final int getMaxLines() {
        return DigitalAdapterTileEntity.MAX_LINES;
    }

    // Speed Controller

    @LuaFunction(mainThread = true)
    public final void setTargetSpeed(String direction, int speed) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return;
        this.tileEntity.setTargetSpeed(dir, speed);
    }

    @LuaFunction(mainThread = true)
    public final int getTargetSpeed(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        return this.tileEntity.getTargetSpeed(dir);
    }

    // Stress Gauge

    @LuaFunction(mainThread = true)
    public final int getKineticStress(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var sg = this.tileEntity.getStressGuage(dir);
        if(sg == null) return 0;
        return (int)sg.getNetworkStress();
    }

    @LuaFunction(mainThread = true)
    public final int getKineticCapacity(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var sg = this.tileEntity.getStressGuage(dir);
        if(sg == null) return 0;
        return (int)sg.getNetworkCapacity();
    }

    // Speed Guage

    @LuaFunction(mainThread = true)
    public final int getKineticSpeed(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var sg = this.tileEntity.getSpeedGuage(dir);
        if(sg == null) return 0;
        return (int)sg.getSpeed();
    }
}
