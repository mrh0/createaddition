package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorTileEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayTileEntity;

public class Peripherals {
	public static ElectricMotorPeripheral createElectricMotorPeripheral(ElectricMotorTileEntity te) {
		return new ElectricMotorPeripheral("electric_motor", te);
	}

	public static PortableEnergyInterfacePeripheral createPortableEnergyInterfacePeripheral(PortableEnergyInterfaceTileEntity te) {
		return new PortableEnergyInterfacePeripheral("portable_energy_interface", te);
	}

	public static ModularAccumulatorPeripheral createModularAccumulatorPeripheral(ModularAccumulatorTileEntity te) {
		return new ModularAccumulatorPeripheral("modular_accumulator", te);
	}

	public static RedstoneRelayPeripheral createRedstoneRelayPeripheral(RedstoneRelayTileEntity te) {
		return new RedstoneRelayPeripheral("redstone_relay", te);
	}

	public static DigitalAdapterPeripheral createDigitalAdapterPeripheral(DigitalAdapterTileEntity te) {
		return new DigitalAdapterPeripheral("digital_adapter", te);
	}
}
