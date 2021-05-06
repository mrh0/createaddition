package com.mrh0.createaddition.compat.computercraft;

import net.minecraftforge.common.capabilities.Capability;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;

public class Peripherals {
	public static boolean isPeripheral(Capability<?> cap) {
		return cap == CAPABILITY_PERIPHERAL;
	}
	
	public static ElectricMotorPeripheral createElectricMotorPeripheral(ElectricMotorTileEntity te) {
		return new ElectricMotorPeripheral("electric_motor", te);
	}
}
