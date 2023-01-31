package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PeripheralProvider implements IPeripheralProvider {

    @Nullable
    @Override
    public IPeripheral getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity block = level.getBlockEntity(blockPos);

        if(block instanceof ElectricMotorTileEntity) {
            return new ElectricMotorPeripheral("electric_motor", (ElectricMotorTileEntity)block);
        }

        return null;
    }
}
