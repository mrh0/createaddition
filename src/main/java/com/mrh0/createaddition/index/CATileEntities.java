package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorRenderer;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorTileEntity;
import com.mrh0.createaddition.blocks.alternator.AlternatorRenderer;
import com.mrh0.createaddition.blocks.alternator.AlternatorTileEntity;
import com.mrh0.createaddition.blocks.connector.ConnectorRenderer;
import com.mrh0.createaddition.blocks.connector.ConnectorTileEntity;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyTileEntity;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorRenderer;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorTileEntity;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerRenderer;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerTileEntity;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayRenderer;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayTileEntity;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillInstance;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillRenderer;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillTileEntity;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilTileEntity;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import team.reborn.energy.api.EnergyStorage;


@SuppressWarnings({"UnstableApiUsage", "removal"})
public class CATileEntities {
	public static final BlockEntityEntry<ElectricMotorTileEntity> ELECTRIC_MOTOR = CreateAddition.registrate()
			.blockEntity("electric_motor", ElectricMotorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ELECTRIC_MOTOR)
			.renderer(() -> ElectricMotorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<AlternatorTileEntity> ALTERNATOR = CreateAddition.registrate()
			.blockEntity("alternator", AlternatorTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CABlocks.ALTERNATOR)
			.renderer(() -> AlternatorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RollingMillTileEntity> ROLLING_MILL = CreateAddition.registrate()
			.blockEntity("rolling_mill", RollingMillTileEntity::new)
			.instance(() -> RollingMillInstance::new)
			.validBlocks(CABlocks.ROLLING_MILL)
			.renderer(() -> RollingMillRenderer::new)
			.register();
	
	public static final BlockEntityEntry<CreativeEnergyTileEntity> CREATIVE_ENERGY = CreateAddition.registrate()
			.blockEntity("creative_energy", CreativeEnergyTileEntity::new)
			.validBlocks(CABlocks.CREATIVE_ENERGY)
			.register();
	
	public static final BlockEntityEntry<ConnectorTileEntity> CONNECTOR = CreateAddition.registrate()
			.blockEntity("connector", ConnectorTileEntity::new)
			.validBlocks(CABlocks.CONNECTOR_COPPER)
			.renderer(() -> ConnectorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<AccumulatorTileEntity> ACCUMULATOR = CreateAddition.registrate()
			.blockEntity("accumulator", AccumulatorTileEntity::new)
			.validBlocks(CABlocks.ACCUMULATOR)
			.renderer(() -> AccumulatorRenderer::new)
			.register();
	
	public static final BlockEntityEntry<RedstoneRelayTileEntity> REDSTONE_RELAY = CreateAddition.registrate()
			.blockEntity("redstone_relay", RedstoneRelayTileEntity::new)
			.validBlocks(CABlocks.REDSTONE_RELAY)
			.renderer(() -> RedstoneRelayRenderer::new)
			.register();
	
	public static final BlockEntityEntry<TeslaCoilTileEntity> TESLA_COIL = CreateAddition.registrate()
			.blockEntity("tesla_coil", TeslaCoilTileEntity::new)
			.validBlocks(CABlocks.TESLA_COIL)
			//.renderer(() -> ChargerRenderer::new)
			.register();
	public static final BlockEntityEntry<LiquidBlazeBurnerTileEntity> LIQUID_BLAZE_BURNER = CreateAddition.registrate()
			.blockEntity("liquid_blaze_burner", LiquidBlazeBurnerTileEntity::new)
			.validBlocks(CABlocks.LIQUID_BLAZE_BURNER)
			.renderer(() -> LiquidBlazeBurnerRenderer::new)
			.register();
	
	public static void register() {
		EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
			if(blockEntity instanceof EnergyTransferable transferable)
				return transferable.getEnergyStorage(context);
			return null;
		});
		FluidStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
			if (blockEntity instanceof FluidTransferable transferable)
				return transferable.getFluidStorage(context);
			return null;
		});
	}
}
