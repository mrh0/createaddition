package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorRenderer;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorTileEntity;
import com.mrh0.createaddition.blocks.alternator.AlternatorRenderer;
import com.mrh0.createaddition.blocks.alternator.AlternatorTileEntity;
import com.mrh0.createaddition.blocks.connector.LargeConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.SmallLightConnectorBlockEntity;
import com.mrh0.createaddition.blocks.connector.base.ConnectorRenderer;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyTileEntity;
import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorRenderer;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerRenderer;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerTileEntity;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorRenderer;
import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorTileEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceInstance;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceRenderer;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayRenderer;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayTileEntity;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillInstance;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillRenderer;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillTileEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilTileEntity;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings({"UnstableApiUsage", "removal"})
public class CATileEntities {
	public static final BlockEntityEntry<ElectricMotorTileEntity> ELECTRIC_MOTOR = CreateAddition.REGISTRATE
			.blockEntity("electric_motor", ElectricMotorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<AlternatorTileEntity> ALTERNATOR = CreateAddition.REGISTRATE
			.blockEntity("alternator", AlternatorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ALTERNATOR)
			.renderer(() -> AlternatorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RollingMillTileEntity> ROLLING_MILL = CreateAddition.REGISTRATE
			.blockEntity("rolling_mill", RollingMillTileEntity::new)
			.instance(() -> RollingMillInstance::new)
			.validBlocks(CABlocks.ROLLING_MILL)
			.renderer(() -> RollingMillRenderer::new)
			.register();
	
	public static final BlockEntityEntry<CreativeEnergyTileEntity> CREATIVE_ENERGY = CreateAddition.REGISTRATE
			.blockEntity("creative_energy", CreativeEnergyTileEntity::new)
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
	
	public static final BlockEntityEntry<AccumulatorTileEntity> ACCUMULATOR = CreateAddition.REGISTRATE
			.blockEntity("accumulator", AccumulatorTileEntity::new)
			.validBlocks(CABlocks.ACCUMULATOR)
			.renderer(() -> AccumulatorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RedstoneRelayTileEntity> REDSTONE_RELAY = CreateAddition.REGISTRATE
			.blockEntity("redstone_relay", RedstoneRelayTileEntity::new)
			.validBlocks(CABlocks.REDSTONE_RELAY)
			.renderer(() -> RedstoneRelayRenderer::new)
			.register();
	
	public static final BlockEntityEntry<TeslaCoilTileEntity> TESLA_COIL = CreateAddition.REGISTRATE
			.blockEntity("tesla_coil", TeslaCoilTileEntity::new)
			.validBlocks(CABlocks.TESLA_COIL)
			.register();

	public static final BlockEntityEntry<LiquidBlazeBurnerTileEntity> LIQUID_BLAZE_BURNER = CreateAddition.REGISTRATE
			.blockEntity("liquid_blaze_burner", LiquidBlazeBurnerTileEntity::new)
			.validBlocks(CABlocks.LIQUID_BLAZE_BURNER)
			.renderer(() -> LiquidBlazeBurnerRenderer::new)
			.register();

	public static final BlockEntityEntry<ModularAccumulatorTileEntity> MODULAR_ACCUMULATOR = CreateAddition.REGISTRATE
			.blockEntity("modular_accumulator", ModularAccumulatorTileEntity::new)
			.validBlocks(CABlocks.MODULAR_ACCUMULATOR)
			.renderer(() -> ModularAccumulatorRenderer::new)
			//.renderer(() -> LiquidBlazeBurnerRenderer::new)
			.register();

	public static final BlockEntityEntry<PortableEnergyInterfaceTileEntity> PORTABLE_ENERGY_INTERFACE = CreateAddition.REGISTRATE
			.blockEntity("portable_energy_interface", PortableEnergyInterfaceTileEntity::new)
			.instance(() -> PortableEnergyInterfaceInstance::new)
			.validBlocks(CABlocks.PORTABLE_ENERGY_INTERFACE)
			.renderer(() -> PortableEnergyInterfaceRenderer::new)
			.register();

	public static final BlockEntityEntry<DigitalAdapterTileEntity> DIGITAL_ADAPTER = CreateAddition.REGISTRATE
			.blockEntity("digital_adapter", DigitalAdapterTileEntity::new)
			.validBlocks(CABlocks.DIGITAL_ADAPTER)
			.register();

	public static void register() {
		EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
			if(blockEntity instanceof EnergyTransferable transferable)
				return transferable.getEnergyStorage(context);
			return null;
		});
		FluidStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
			if (blockEntity instanceof SidedStorageBlockEntity  transferable)
				return transferable.getFluidStorage(context);
			return null;
		});
	}
}