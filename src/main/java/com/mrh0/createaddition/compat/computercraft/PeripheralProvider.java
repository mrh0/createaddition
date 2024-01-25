package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterBlockEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlockEntity;
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

        if(be instanceof ElectricMotorBlockEntity electricMotorTile) {
            return Peripherals.createElectricMotorPeripheral(electricMotorTile);
        } else if (be instanceof PortableEnergyInterfaceBlockEntity portableEnergyInterfaceTile) {
            return Peripherals.createPortableEnergyInterfacePeripheral(portableEnergyInterfaceTile);
        } else if (be instanceof ModularAccumulatorBlockEntity modularAccumulatorTile) {
            return Peripherals.createModularAccumulatorPeripheral(modularAccumulatorTile);
        } else if (be instanceof RedstoneRelayBlockEntity redstoneRelayTile) {
            return Peripherals.createRedstoneRelayPeripheral(redstoneRelayTile);
        } else if (be instanceof DigitalAdapterBlockEntity digitalAdapterTileEntity) {
            return Peripherals.createDigitalAdapterPeripheral(digitalAdapterTileEntity);
        }

        return null;
    }
}
