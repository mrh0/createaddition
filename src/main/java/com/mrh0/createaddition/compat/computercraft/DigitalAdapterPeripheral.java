package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_display_link_adapter.DigitalAdapterTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
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
        this.tileEntity.setTextLine(this.tileEntity.getLine(), new TextComponent(text));
        this.tileEntity.incrementLine();
    }

    @LuaFunction(mainThread = true)
    public final void write(String text) {
        this.tileEntity.append(this.tileEntity.getLine(), new TextComponent(text));
    }

    @LuaFunction(mainThread = true)
    public final int getLine() {
        return this.tileEntity.getLine();
    }

    @LuaFunction(mainThread = true)
    public final int setLine(int ln) {
        return this.tileEntity.setLine(ln);
    }
}
