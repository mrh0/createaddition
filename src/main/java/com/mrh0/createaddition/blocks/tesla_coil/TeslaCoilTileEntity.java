package com.mrh0.createaddition.blocks.tesla_coil;

import java.util.List;

import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public class TeslaCoilTileEntity extends BaseElectricTileEntity {

	public TeslaCoilTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1000, 1000, 1000);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != getBlockState().getValue(TeslaCoil.FACING);
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
}
