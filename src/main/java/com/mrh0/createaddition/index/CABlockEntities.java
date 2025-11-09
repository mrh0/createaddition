package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.alternator.AlternatorBlockEntity;
import com.mrh0.createaddition.blocks.connector.LargeConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallLightConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.base.ConnectorRenderer;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlockEntity;
import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterBlockEntity;
import com.mrh0.createaddition.blocks.electric_motor.*;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.*;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlockEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorRenderer;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceRenderer;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlockEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlockEntity;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlockEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlockEntity;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.alternator.*;
import com.mrh0.createaddition.blocks.rolling_mill.*;
import com.mrh0.createaddition.blocks.redstone_relay.*;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CABlockEntities {
	public static final BlockEntityEntry<ElectricMotorBlockEntity> ELECTRIC_MOTOR = CreateAddition.REGISTRATE
			.blockEntity("electric_motor", ElectricMotorBlockEntity::new)
			.visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<AlternatorBlockEntity> ALTERNATOR = CreateAddition.REGISTRATE
			.blockEntity("alternator", AlternatorBlockEntity::new)
			.visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
			.validBlocks(CABlocks.ALTERNATOR)
			.renderer(() -> AlternatorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RollingMillBlockEntity> ROLLING_MILL = CreateAddition.REGISTRATE
			.blockEntity("rolling_mill", RollingMillBlockEntity::new)
			.visual(() -> RollingMillVisual::new)
			.validBlocks(CABlocks.ROLLING_MILL)
			.renderer(() -> RollingMillRenderer::new)
			.register();
	
	public static final BlockEntityEntry<CreativeEnergyBlockEntity> CREATIVE_ENERGY = CreateAddition.REGISTRATE
			.blockEntity("creative_energy", CreativeEnergyBlockEntity::new)
			.validBlocks(CABlocks.CREATIVE_ENERGY)
			.register();
	
	public static final BlockEntityEntry<SmallConnectorBlockEntity> SMALL_CONNECTOR = CreateAddition.REGISTRATE
			.blockEntity("connector", SmallConnectorBlockEntity::new)
			.validBlocks(CABlocks.SMALL_CONNECTOR)
			.renderer(() -> ConnectorRenderer::new)
			.register();

	public static final BlockEntityEntry<SmallLightConnectorBlockEntity> SMALL_LIGHT_CONNECTOR = CreateAddition.REGISTRATE
			.blockEntity("small_light_connector", SmallLightConnectorBlockEntity::new)
			.validBlocks(CABlocks.SMALL_LIGHT_CONNECTOR)
			.renderer(() -> ConnectorRenderer::new)
			.register();

	public static final BlockEntityEntry<LargeConnectorBlockEntity> LARGE_CONNECTOR = CreateAddition.REGISTRATE
			.blockEntity("large_connector", LargeConnectorBlockEntity::new)
			.validBlocks(CABlocks.LARGE_CONNECTOR)
			.renderer(() -> ConnectorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RedstoneRelayBlockEntity> REDSTONE_RELAY = CreateAddition.REGISTRATE
			.blockEntity("redstone_relay", RedstoneRelayBlockEntity::new)
			.validBlocks(CABlocks.REDSTONE_RELAY)
			.renderer(() -> RedstoneRelayRenderer::new)
			.register();
	
	public static final BlockEntityEntry<TeslaCoilBlockEntity> TESLA_COIL = CreateAddition.REGISTRATE
			.blockEntity("tesla_coil", TeslaCoilBlockEntity::new)
			.validBlocks(CABlocks.TESLA_COIL)
			.register();
	
	public static final BlockEntityEntry<LiquidBlazeBurnerBlockEntity> LIQUID_BLAZE_BURNER = CreateAddition.REGISTRATE
			.blockEntity("liquid_blaze_burner", LiquidBlazeBurnerBlockEntity::new)
			.validBlocks(CABlocks.LIQUID_BLAZE_BURNER)
			.renderer(() -> LiquidBlazeBurnerRenderer::new)
			.register();
	
	public static final BlockEntityEntry<ModularAccumulatorBlockEntity> MODULAR_ACCUMULATOR = CreateAddition.REGISTRATE
			.blockEntity("modular_accumulator", ModularAccumulatorBlockEntity::new)
			.validBlocks(CABlocks.MODULAR_ACCUMULATOR)
			.renderer(() -> ModularAccumulatorRenderer::new)
			.register();

	public static final BlockEntityEntry<PortableEnergyInterfaceBlockEntity> PORTABLE_ENERGY_INTERFACE = CreateAddition.REGISTRATE
			.blockEntity("portable_energy_interface", PortableEnergyInterfaceBlockEntity::new)
			// .visual(() -> PortableEnergyInterfaceVisual::new)
			.validBlocks(CABlocks.PORTABLE_ENERGY_INTERFACE)
			.renderer(() -> PortableEnergyInterfaceRenderer::new)
			.register();

	public static final BlockEntityEntry<DigitalAdapterBlockEntity> DIGITAL_ADAPTER = CreateAddition.REGISTRATE
			.blockEntity("digital_adapter", DigitalAdapterBlockEntity::new)
			.validBlocks(CABlocks.DIGITAL_ADAPTER)
			.register();
	
	public static void register() {}
}