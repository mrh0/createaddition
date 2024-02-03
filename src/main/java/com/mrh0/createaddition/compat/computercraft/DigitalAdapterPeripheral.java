package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterBlockEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DigitalAdapterPeripheral implements IPeripheral {
    private final String type;
    private final DigitalAdapterBlockEntity tileEntity;

    public DigitalAdapterPeripheral(String type, DigitalAdapterBlockEntity tileEntity) {
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
        return DigitalAdapterBlockEntity.MAX_LINES;
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
        return AllConfigs.server().kinetics.maxRotationSpeed.get();
    }

    // Pulley

    @LuaFunction(mainThread = true)
    public final int getPulleyDistance(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var rp = this.tileEntity.getRopePulley(dir);
        var hp = this.tileEntity.getHosePulley(dir);
        var ep = this.tileEntity.getElevatorPulley(dir);
        if(rp != null) {
            return (int) rp.getInterpolatedOffset(.5f);
        }
        else if(hp != null) {
            return (int) hp.getInterpolatedOffset(.5f);
        }
        else if(ep != null) {
            return (int) ep.getInterpolatedOffset(.5f);
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

    public final @Nullable ElevatorContraption getElevatorContraption(ElevatorPulleyBlockEntity ep) {
        if(ep == null) return null;
        if(ep.movedContraption == null) return null;
        if(ep.movedContraption.getContraption() == null) return null;
        if(!(ep.movedContraption.getContraption() instanceof ElevatorContraption ec)) return null;
        return ec;
    }

    @LuaFunction(mainThread = true)
    public final int getElevatorFloor(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var ep = this.tileEntity.getElevatorPulley(dir);
        if(ep == null) return 0;
        var ec = getElevatorContraption(ep);
        if(ec == null) return 0;

        for(int i = 0; i < ec.namesList.size(); ++i) {
            if ((int)((IntAttached)ec.namesList.get(i)).getFirst() == ec.getCurrentTargetY(ep.getLevel())) {
                return i;
            }
        }
        return 0;
    }

    @LuaFunction(mainThread = true)
    public final int getElevatorFloors(String direction) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var ep = this.tileEntity.getElevatorPulley(dir);
        if(ep == null) return 0;
        var ec = getElevatorContraption(ep);
        if(ec == null) return 0;
        return ec.namesList.size();
    }

    @LuaFunction(mainThread = true)
    public final String getElevatorFloorName(String direction, int index) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return String.valueOf(index);
        var ep = this.tileEntity.getElevatorPulley(dir);
        if(ep == null) return String.valueOf(index);
        var ec = getElevatorContraption(ep);
        if(ec == null) return String.valueOf(index);

        if(index >= ec.namesList.size()) return String.valueOf(index);
        if(index < 0) return String.valueOf(index);
        return ec.namesList.get(index).getSecond().getFirst();
    }

    @LuaFunction(mainThread = true)
    public final int gotoElevatorFloor(String direction, int index) {
        Direction dir = Helpers.nameToDir(direction);
        if(dir == null) return 0;
        var ep = this.tileEntity.getElevatorPulley(dir);
        if(ep == null) return 0;
        var ec = getElevatorContraption(ep);
        if(ec == null) return 0;

        if(index >= ec.namesList.size()) return 0;
        if(index < 0) return 0;

        var level = this.tileEntity.getLevel();

        int oldTargetY = ec.getCurrentTargetY(level);
        int targetY = ec.namesList.get(index).getFirst();

        ElevatorColumn elevatorColumn = ElevatorColumn.get(level, ec.getGlobalColumn());
        if (!ec.isTargetUnreachable(targetY)) {
            BlockPos pos = elevatorColumn.contactAt(targetY);
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block instanceof ElevatorContactBlock) {
                ElevatorContactBlock ecb = (ElevatorContactBlock)block;
                ecb.callToContactAndUpdate(elevatorColumn, blockState, level, pos, false);
            }
        }

        return targetY - oldTargetY;
    }

    @LuaFunction(mainThread = true)
    public final float getDurationAngle(int deg, int rpm) throws LuaException {
        return ElectricMotorBlockEntity.getDurationAngle(deg, 0, rpm) / 20f;
    }

    @LuaFunction(mainThread = true)
    public final float getDurationDistance(int blocks, int rpm) throws LuaException {
        return ElectricMotorBlockEntity.getDurationDistance(blocks, 0, rpm) / 20f;
    }
}
