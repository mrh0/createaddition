package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlock;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlockEntity;
import com.mrh0.createaddition.config.Config;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneRelayPeripheral implements IPeripheral {
    private final String type;
    private final RedstoneRelayBlockEntity tileEntity;

    public RedstoneRelayPeripheral(String type, RedstoneRelayBlockEntity tileEntity) {
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
    public int getMaxInsert() {
        return Config.SMALL_CONNECTOR_MAX_INPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getMaxExtract() {
        return Config.SMALL_CONNECTOR_MAX_OUTPUT.get();
    }

    @LuaFunction(mainThread = true)
    public int getThroughput() {
        return this.tileEntity.getThroughput();
    }

    @LuaFunction(mainThread = true)
    public boolean isPowered() {
        return this.tileEntity.getBlockState().getValue(RedstoneRelayBlock.POWERED);
    }
}