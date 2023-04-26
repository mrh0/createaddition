package com.mrh0.createaddition.blocks.digital_adapter;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.computercraft.DigitalAdapterPeripheral;
import com.mrh0.createaddition.compat.computercraft.Peripherals;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.piston.MechanicalPistonTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyTileEntity;
import com.simibubi.create.content.contraptions.fluids.actors.HosePulleyTileEntity;
import com.simibubi.create.content.contraptions.relays.advanced.SpeedControllerTileEntity;
import com.simibubi.create.content.contraptions.relays.gauge.SpeedGaugeTileEntity;
import com.simibubi.create.content.contraptions.relays.gauge.StressGaugeTileEntity;
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

public class DigitalAdapterTileEntity extends BlockEntity {
    public final List<MutableComponent> textLines;
    public static final int MAX_LINES = 16;
    public static final MutableComponent EMPTY_LINE = Component.literal("");

    protected LazyOptional<DigitalAdapterPeripheral> peripheral;

    public DigitalAdapterTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        textLines = new ArrayList<>();
        for(int i = 0; i < MAX_LINES; i++) textLines.add(EMPTY_LINE);

        if (CreateAddition.CC_ACTIVE)
            this.peripheral = LazyOptional.of(() -> Peripherals.createDigitalAdapterPeripheral(this));
    }

    private int line = 1;

    public void incrementLine() {
        line = Math.min(line + 1, DigitalAdapterTileEntity.MAX_LINES);
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
        return line = ln < 1 || ln > DigitalAdapterTileEntity.MAX_LINES ? line : ln;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (CreateAddition.CC_ACTIVE && Peripherals.isPeripheral(cap)) return this.peripheral.cast();
        return super.getCapability(cap, side);
    }

    public SpeedControllerTileEntity getSpeedController(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof SpeedControllerTileEntity scte) return scte;
        return null;
    }

    public PulleyTileEntity getRopePulley(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof PulleyTileEntity pte) return pte;
        return null;
    }

    public HosePulleyTileEntity getHosePulley(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof HosePulleyTileEntity pte) return pte;
        return null;
    }

    public MechanicalPistonTileEntity getMechanicalPiston(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof MechanicalPistonTileEntity mpte) return mpte;
        return null;
    }

    public MechanicalBearingTileEntity getMechanicalBearing(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof MechanicalBearingTileEntity mpte) return mpte;
        return null;
    }

    public StressGaugeTileEntity getStressGauge(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof StressGaugeTileEntity sgte) return sgte;
        return null;
    }

    public SpeedGaugeTileEntity getSpeedGauge(Direction dir) {
        BlockEntity be = this.level.getBlockEntity(getBlockPos().relative(dir));
        if(be == null) return null;
        if(be instanceof SpeedGaugeTileEntity sgte) return sgte;
        return null;
    }

    public void setTargetSpeed(Direction dir, int speed) {
        SpeedControllerTileEntity scte = getSpeedController(dir);
        if(scte == null) return;
        ISpeedControllerAdapter sts = (ISpeedControllerAdapter)((Object)scte);
        sts.setTargetSpeed(speed);
    }

    public int getTargetSpeed(Direction dir) {
        SpeedControllerTileEntity scte = getSpeedController(dir);
        if(scte == null) return 0;
        ISpeedControllerAdapter sts = (ISpeedControllerAdapter)((Object)scte);
        return sts.getTargetSpeed();
    }
}
