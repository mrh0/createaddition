package com.mrh0.createaddition.compat.computercraft;

import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class Peripherals {
	public static boolean isPeripheral(Level world, BlockPos pos, Direction side) {
		return side != null && dan200.computercraft.shared.Peripherals.getPeripheral(world, pos, side) != null;
	}

	public static ElectricMotorPeripheral createElectricMotorPeripheral(ElectricMotorTileEntity te) {
		return new ElectricMotorPeripheral("electric_motor", te);
	}
}
