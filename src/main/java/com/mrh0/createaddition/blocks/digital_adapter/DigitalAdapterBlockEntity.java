package com.mrh0.createaddition.blocks.digital_adapter;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.computercraft.DigitalAdapterPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DigitalAdapterBlockEntity extends BlockEntity {
    public final List<MutableComponent> textLines;
    public static final int MAX_LINES = 16;
    public static final MutableComponent EMPTY_LINE = Component.literal("");

    protected LazyOptional<DigitalAdapterPeripheral> peripheral;

    public DigitalAdapterBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        textLines = new ArrayList<>();
        for(int i = 0; i < MAX_LINES; i++) textLines.add(EMPTY_LINE);

        if (CreateAddition.CC_ACTIVE)
            this.peripheral = LazyOptional.of(() -> Peripherals.createDigitalAdapterPeripheral(this));
    }

    private int line = 1;

    public void incrementLine() {
        line = Math.min(line + 1, DigitalAdapterBlockEntity.MAX_LINES);
    }

    public void setTextLine(int ln, MutableComponent text) {
        if(ln < 1 || ln > MAX_LINES) return;
        textLines.set(ln-1, text);
    }

    public MutableComponent getTextLine(int ln) {
        if(ln < 1 || ln > MAX_LINES) return EMPTY_LINE;
        return textLines.get(ln-1);
    }

    public void clearLine(int ln) {
        setTextLine(ln, EMPTY_LINE);
    }

    public void clearAll() {
        for(int i = 1; i < MAX_LINES+1; i++)
            clearLine(i);
    }

    public void append(int ln, MutableComponent text) {
        setTextLine(ln, getTextLine(ln).append(text));
    }

    public int getLine() {
        return line;
    }

    public int setLine(int ln) {
        return line = ln < 1 || ln > DigitalAdapterBlockEntity.MAX_LINES ? line : ln;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (CreateAddition.CC_ACTIVE && Peripherals.isPeripheral(cap)) return this.peripheral.cast();
        return super.getCapability(cap, side);
    }

    public SpeedControllerBlockEntity getSpeedController(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof SpeedControllerBlockEntity scte) return scte;
        return null;
    }

    public PulleyBlockEntity getRopePulley(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof PulleyBlockEntity pte) return pte;
        return null;
    }

    public HosePulleyBlockEntity getHosePulley(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof HosePulleyBlockEntity pte) return pte;
        return null;
    }

    public ElevatorPulleyBlockEntity getElevatorPulley(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof ElevatorPulleyBlockEntity epbe) return epbe;
        return null;
    }

    public MechanicalPistonBlockEntity getMechanicalPiston(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof MechanicalPistonBlockEntity mpte) return mpte;
        return null;
    }

    public MechanicalBearingBlockEntity getMechanicalBearing(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof MechanicalBearingBlockEntity mpte) return mpte;
        return null;
    }

    public StressGaugeBlockEntity getStressGauge(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof StressGaugeBlockEntity sgte) return sgte;
        return null;
    }

    public SpeedGaugeBlockEntity getSpeedGauge(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof SpeedGaugeBlockEntity sgte) return sgte;
        return null;
    }

    public void setTargetSpeed(Direction dir, int speed) {
        SpeedControllerBlockEntity scte = getSpeedController(dir);
        if(scte == null) return;
        ISpeedControllerAdapter sts = (ISpeedControllerAdapter)((Object)scte);
        sts.setTargetSpeed(speed);
    }

    public int getTargetSpeed(Direction dir) {
        SpeedControllerBlockEntity scte = getSpeedController(dir);
        if(scte == null) return 0;
        ISpeedControllerAdapter sts = (ISpeedControllerAdapter)((Object)scte);
        return sts.getTargetSpeed();
    }
}
