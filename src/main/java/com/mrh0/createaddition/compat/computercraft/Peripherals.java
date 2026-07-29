package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterBlockEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlockEntity;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;
import com.mrh0.createaddition.index.CABlockEntities;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class Peripherals {
	public static void registerPeripheralCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.ELECTRIC_MOTOR.get(),
				(be, dir) -> createElectricMotorPeripheral(be)
		);

		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.PORTABLE_ENERGY_INTERFACE.get(),
				(be, dir) -> createPortableEnergyInterfacePeripheral(be)
		);

		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.MODULAR_ACCUMULATOR.get(),
				(be, dir) -> createModularAccumulatorPeripheral(be)
		);

		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.REDSTONE_RELAY.get(),
				(be, dir) -> createRedstoneRelayPeripheral(be)
		);

		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.DIGITAL_ADAPTER.get(),
				(be, dir) -> createDigitalAdapterPeripheral(be)
		);

		event.registerBlockEntity(
				PeripheralCapability.get(),
				CABlockEntities.SERVO_MOTOR.get(),
				(be, dir) -> new ServoMotorPeripheral(be)
		);
	}
	
	public static ElectricMotorPeripheral createElectricMotorPeripheral(ElectricMotorBlockEntity te) {
		return new ElectricMotorPeripheral("electric_motor", te);
	}

	public static PortableEnergyInterfacePeripheral createPortableEnergyInterfacePeripheral(PortableEnergyInterfaceBlockEntity te) {
		return new PortableEnergyInterfacePeripheral("portable_energy_interface", te);
	}

	public static ModularAccumulatorPeripheral createModularAccumulatorPeripheral(ModularAccumulatorBlockEntity te) {
		return new ModularAccumulatorPeripheral("modular_accumulator", te);
	}

	public static RedstoneRelayPeripheral createRedstoneRelayPeripheral(RedstoneRelayBlockEntity te) {
		return new RedstoneRelayPeripheral("redstone_relay", te);
	}

	public static DigitalAdapterPeripheral createDigitalAdapterPeripheral(DigitalAdapterBlockEntity te) {
		return new DigitalAdapterPeripheral("digital_adapter", te);
	}
}
