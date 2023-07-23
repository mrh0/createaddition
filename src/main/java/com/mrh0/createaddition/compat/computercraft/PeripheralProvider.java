package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorTileEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayTileEntity;
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
        BlockEntity be = level.getBlockEntity(blockPos);

        if(be instanceof ElectricMotorTileEntity electricMotorTile) {
            return Peripherals.createElectricMotorPeripheral(electricMotorTile);
        } else if (be instanceof PortableEnergyInterfaceTileEntity portableEnergyInterfaceTile) {
            return Peripherals.createPortableEnergyInterfacePeripheral(portableEnergyInterfaceTile);
        } else if (be instanceof ModularAccumulatorTileEntity modularAccumulatorTile) {
            return Peripherals.createModularAccumulatorPeripheral(modularAccumulatorTile);
        } else if (be instanceof RedstoneRelayTileEntity redstoneRelayTile) {
            return Peripherals.createRedstoneRelayPeripheral(redstoneRelayTile);
        } else if (be instanceof DigitalAdapterTileEntity digitalAdapterTileEntity) {
            return Peripherals.createDigitalAdapterPeripheral(digitalAdapterTileEntity);
        }

        return null;
    }
}
