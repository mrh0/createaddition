package com.mrh0.createaddition.compat.immersive_engineering;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler.IExternalHeatable;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class IEHeaterOptional {
	public static boolean externalHeater(TileEntity te, IEnergyStorage energyStorage) {
		int consumed = 0;
		
		if(te instanceof IExternalHeatable)
			consumed = ((IExternalHeatable)te).doHeatTick(energyStorage.getEnergyStored(), true);
		else
			return false;

		if(consumed > 0)
			energyStorage.extractEnergy(consumed, false);
		return true;
	}
}
