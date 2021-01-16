package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.electric_motor.*;
import com.simibubi.create.Create;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class CATileEntities {
	public static final TileEntityEntry<ElectricMotorTileEntity> ELECTRIC_MOTOR = Create.registrate()
			.tileEntity("electric_motor", ElectricMotorTileEntity::new)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static void register() {}
}
