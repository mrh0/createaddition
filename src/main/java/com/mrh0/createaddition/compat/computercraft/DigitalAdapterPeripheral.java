package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CServer;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        this.tileEntity.setTextLine(this.tileEntity.getLine(), Component.literal(text.substring(0, Math.min(text.length(), 128))));
        this.tileEntity.incrementLine();
    }

    /*
    @LuaFunction(mainThread = true)
    public final void write(String text) {
        this.tileEntity.append(this.tileEntity.getLine(), new TextComponent(text));
    }
    */

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
        var sg = this.tileEntity.getStressGauge(dir);
        if(sg == null) return 0;
        return (int) sg.getNetworkStress();
    }

    @LuaFunction(mainThread = true)
    public final int getKineticCapacity(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var sg = this.tileEntity.getStressGauge(dir);
        if(sg == null) return 0;
        return (int) sg.getNetworkCapacity();
    }

    // Speed Gauge

    @LuaFunction(mainThread = true)
    public final int getKineticSpeed(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var sg = this.tileEntity.getSpeedGauge(dir);
        if(sg == null) return 0;
        return (int) sg.getSpeed();
    }

    @LuaFunction(mainThread = true)
    public final int getKineticTopSpeed() {
        return AllConfigs.SERVER.kinetics.maxRotationSpeed.get();
    }

    // Pulley

    @LuaFunction(mainThread = true)
    public final int getPulleyDistance(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var rp = this.tileEntity.getRopePulley(dir);
        var hp = this.tileEntity.getHosePulley(dir);
        if(rp != null) {
            return (int) rp.getInterpolatedOffset(.5f);
        }
        else if(hp != null) {
            return (int) hp.getInterpolatedOffset(.5f);
        }
        else {
            return 0;
        }
    }

    @LuaFunction(mainThread = true)
    public final int getPistonDistance(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var mp = this.tileEntity.getMechanicalPiston(dir);
        if(mp == null) return 0;
        return (int) mp.getInterpolatedOffset(.5f);
    }

    @LuaFunction(mainThread = true)
    public final int getBearingAngle(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var mp = this.tileEntity.getMechanicalBearing(dir);
        if(mp == null) return 0;
        return (int) mp.getInterpolatedAngle(.5f);
    }

    @LuaFunction(mainThread = true)
    public final float getDurationAngle(int deg, int rpm) throws LuaException {
        return ElectricMotorTileEntity.getDurationAngle(deg, 0, rpm) / 20f;
    }

    @LuaFunction(mainThread = true)
    public final float getDurationDistance(int blocks, int rpm) throws LuaException {
        return ElectricMotorTileEntity.getDurationDistance(blocks, 0, rpm) / 20f;
    }
}
