package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.alternator.AlternatorTileEntity;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.*;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillTileEntity;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.alternator.*;
import com.mrh0.createaddition.blocks.rolling_mill.*;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class CATileEntities {
	public static final TileEntityEntry<ElectricMotorTileEntity> ELECTRIC_MOTOR = CreateAddition.registrate()
			.tileEntity("electric_motor", ElectricMotorTileEntity::new)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static final TileEntityEntry<AlternatorTileEntity> ALTERNATOR = CreateAddition.registrate()
			.tileEntity("alternator", AlternatorTileEntity::new)
			.validBlocks(CABlocks.ALTERNATOR)
			.renderer(() -> AlternatorRenderer::new)
			.register();
	
	public static final TileEntityEntry<RollingMillTileEntity> ROLLING_MILL = CreateAddition.registrate()
			.tileEntity("rolling_mill", RollingMillTileEntity::new)
			.validBlocks(CABlocks.ROLLING_MILL)
			.renderer(() -> RollingMillRenderer::new)
			.register();
	
	public static final TileEntityEntry<CreativeEnergyTileEntity> CREATIVE_ENERGY = CreateAddition.registrate()
			.tileEntity("creative_energy", CreativeEnergyTileEntity::new)
			.validBlocks(CABlocks.CREATIVE_ENERGY)
			.register();
	
	
	public static void register() {}
}
