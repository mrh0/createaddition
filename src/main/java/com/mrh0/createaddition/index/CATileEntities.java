package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.alternator.AlternatorTileEntity;
import com.mrh0.createaddition.blocks.charger.ChargerTileEntity;
import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyTileEntity;
import com.mrh0.createaddition.blocks.crude_burner.CrudeBurnerTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.*;
import com.mrh0.createaddition.blocks.furnace_burner.FurnaceBurnerTileEntity;
import com.mrh0.createaddition.blocks.heater.HeaterTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayTileEntity;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillTileEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilTileEntity;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorTileEntity;
import com.mrh0.createaddition.blocks.alternator.*;
import com.mrh0.createaddition.blocks.rolling_mill.*;
import com.mrh0.createaddition.blocks.accumulator.*;
import com.mrh0.createaddition.blocks.connector.*;
import com.mrh0.createaddition.blocks.redstone_relay.*;
import com.mrh0.createaddition.blocks.charger.*;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class CATileEntities {
	public static final TileEntityEntry<ElectricMotorTileEntity> ELECTRIC_MOTOR = CreateAddition.registrate()
			.tileEntity("electric_motor", ElectricMotorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static final TileEntityEntry<AlternatorTileEntity> ALTERNATOR = CreateAddition.registrate()
			.tileEntity("alternator", AlternatorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ALTERNATOR)
			.renderer(() -> AlternatorRenderer::new)
			.register();
	
	public static final TileEntityEntry<RollingMillTileEntity> ROLLING_MILL = CreateAddition.registrate()
			.tileEntity("rolling_mill", RollingMillTileEntity::new)
			.instance(() -> RollingMillInstance::new)
			.validBlocks(CABlocks.ROLLING_MILL)
			.renderer(() -> RollingMillRenderer::new)
			.register();
	
	public static final TileEntityEntry<CreativeEnergyTileEntity> CREATIVE_ENERGY = CreateAddition.registrate()
			.tileEntity("creative_energy", CreativeEnergyTileEntity::new)
			.validBlocks(CABlocks.CREATIVE_ENERGY)
			.register();
	
	public static final TileEntityEntry<ConnectorTileEntity> CONNECTOR = CreateAddition.registrate()
			.tileEntity("connector", ConnectorTileEntity::new)
			.validBlocks(CABlocks.CONNECTOR)
			.renderer(() -> ConnectorRenderer::new)
			.register();
	
	public static final TileEntityEntry<HeaterTileEntity> HEATER = CreateAddition.registrate()
			.tileEntity("heater", HeaterTileEntity::new)
			.validBlocks(CABlocks.HEATER)
			.register();
	
	public static final TileEntityEntry<AccumulatorTileEntity> ACCUMULATOR = CreateAddition.registrate()
			.tileEntity("accumulator", AccumulatorTileEntity::new)
			.validBlocks(CABlocks.ACCUMULATOR)
			.renderer(() -> AccumulatorRenderer::new)
			.register();
	
	public static final TileEntityEntry<RedstoneRelayTileEntity> REDSTONE_RELAY = CreateAddition.registrate()
			.tileEntity("redstone_relay", RedstoneRelayTileEntity::new)
			.validBlocks(CABlocks.REDSTONE_RELAY)
			.renderer(() -> RedstoneRelayRenderer::new)
			.register();
	
	public static final TileEntityEntry<FurnaceBurnerTileEntity> FURNACE_BURNER = CreateAddition.registrate()
			.tileEntity("furnace_burner", FurnaceBurnerTileEntity::new)
			.validBlocks(CABlocks.FURNACE_BURNER)
			.register();
	
	public static final TileEntityEntry<CrudeBurnerTileEntity> CRUDE_BURNER = CreateAddition.registrate()
			.tileEntity("crude_burner", CrudeBurnerTileEntity::new)
			.validBlocks(CABlocks.CRUDE_BURNER)
			.register();
	
	public static final TileEntityEntry<ChargerTileEntity> CHARGER = CreateAddition.registrate()
			.tileEntity("charger", ChargerTileEntity::new)
			.validBlocks(CABlocks.CHARGER)
			.renderer(() -> ChargerRenderer::new)
			.register();
	
	public static final TileEntityEntry<TeslaCoilTileEntity> TESLA_COIL = CreateAddition.registrate()
			.tileEntity("charger", TeslaCoilTileEntity::new)
			.validBlocks(CABlocks.TESLA_COIL)
			//.renderer(() -> ChargerRenderer::new)
			.register();
	
	public static void register() {}
}
