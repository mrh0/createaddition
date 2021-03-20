package com.mrh0.createaddition.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Config {

	public static final String CATAGORY_GENERAL = "general";
	public static final String CATAGORY_ELECTRIC_MOTOR = "electric_motor";
	public static final String CATAGORY_ALTERNATOR = "alternator";
	public static final String CATAGORY_ROLLING_MILL = "rolling_mill";
	public static final String CATAGORY_HEATER = "heater";
	public static final String CATAGORY_WIRES = "wires";
	
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MIN_RPM;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_RPM;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_CAPACITY;
	public static ForgeConfigSpec.IntValue FE_TO_SU;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_STRESS;
	
	public static ForgeConfigSpec.IntValue ALTERNATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ALTERNATOR_CAPACITY;
	public static ForgeConfigSpec.IntValue ALTERNATOR_STRESS;
	public static ForgeConfigSpec.DoubleValue ALTERNATOR_EFFICIENCY;
	
	public static ForgeConfigSpec.IntValue ROLLING_MILL_PROCESSING_DURATION;
	public static ForgeConfigSpec.IntValue ROLLING_MILL_STRESS;
	
	public static ForgeConfigSpec.IntValue HEATER_MAX_INPUT;
	public static ForgeConfigSpec.IntValue HEATER_CAPACITY;
	public static ForgeConfigSpec.IntValue HEATER_NORMAL_CONSUMPTION;
	public static ForgeConfigSpec.IntValue HEATER_FURNACE_ENGINE_CONSUMPTION;
	public static ForgeConfigSpec.BooleanValue HEATER_FURNACE_ENGINE_ENABLED;
	
	public static ForgeConfigSpec.IntValue CONNECTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue CONNECTOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue CONNECTOR_CAPACITY;
	
	public static ForgeConfigSpec.DoubleValue COPPER_WIRE_LOSS;
	public static ForgeConfigSpec.DoubleValue GOLD_WIRE_LOSS;
	
	static {
		
		COMMON_BUILDER.comment("General Settings").push(CATAGORY_GENERAL);
		
		FE_TO_SU = COMMON_BUILDER.comment("Forge Energy conversion rate (in FE/t at max RPM).")
				.defineInRange("fe_conversion", 80, 0, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Electric Motor").push(CATAGORY_ELECTRIC_MOTOR);
		
		ELECTRIC_MOTOR_MIN_RPM = COMMON_BUILDER.comment("Electric Motor minimum RPM.")
				.defineInRange("motor_min_rpm", 32, 0, 256);
		
		ELECTRIC_MOTOR_MAX_RPM = COMMON_BUILDER.comment("Electric Motor maximum RPM.")
				.defineInRange("motor_max_rpm", 256, 1, 256);
		
		ELECTRIC_MOTOR_MAX_INPUT = COMMON_BUILDER.comment("Electric Motor max input in FE (Energy transfer not consumption).")
				.defineInRange("motor_max_input", 8192, 0, Integer.MAX_VALUE);
		
		ELECTRIC_MOTOR_CAPACITY = COMMON_BUILDER.comment("Electric Motor internal capacity in FE.")
				.defineInRange("motor_capacity", 16000, 0, Integer.MAX_VALUE);
		
		ELECTRIC_MOTOR_STRESS = COMMON_BUILDER.comment("Electric Motor generated base stress (Not implemented).")
				.defineInRange("motor_stress", 16, 0, 256);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Alternator").push(CATAGORY_ALTERNATOR);
		
		ALTERNATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Alternator max input in FE (Energy transfer not generation).")
				.defineInRange("generator_max_output", 8192, 0, Integer.MAX_VALUE);
		
		ALTERNATOR_CAPACITY = COMMON_BUILDER.comment("Alternator internal capacity in FE.")
				.defineInRange("generator_capacity", 32000, 0, Integer.MAX_VALUE);
		
		ALTERNATOR_STRESS = COMMON_BUILDER.comment("Alternator base stress impact.")
				.defineInRange("generator_stress", 16, 0, 1024);
		
		ALTERNATOR_EFFICIENCY = COMMON_BUILDER.comment("Alternator efficiency relative to base conversion rate.")
				.defineInRange("generator_efficiency", 0.75d, 0.01d, 0.99d);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Rolling Mill").push(CATAGORY_ROLLING_MILL);
		
		ROLLING_MILL_PROCESSING_DURATION = COMMON_BUILDER.comment("Rolling Mill duration in ticks.")
				.defineInRange("rolling_mill_processing_duration", 100, 0, Integer.MAX_VALUE);
		
		ROLLING_MILL_STRESS = COMMON_BUILDER.comment("Rolling Mill base stress impact.")
				.defineInRange("rolling_mill_stress", 16, 0, 1024);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Heater").push(CATAGORY_HEATER);
		
		HEATER_MAX_INPUT = COMMON_BUILDER.comment("Induction Heater max input in FE (Energy transfer not consumption).")
				.defineInRange("heater_max_input", 8192, 0, Integer.MAX_VALUE);
		
		HEATER_CAPACITY = COMMON_BUILDER.comment("Induction Heater internal capacity in FE.")
				.defineInRange("heater_capacity", 16000, 0, Integer.MAX_VALUE);
		
		HEATER_NORMAL_CONSUMPTION = COMMON_BUILDER.comment("Induction Heater normal consumption rate in FE/t.")
				.defineInRange("heater_normal_consumption", 16, 0, Integer.MAX_VALUE);
		
		HEATER_FURNACE_ENGINE_CONSUMPTION = COMMON_BUILDER.comment("Induction Heater when attached to a Furnace Engine consumption rate in FE/t.")
				.defineInRange("heater_furnace_engine_consumption", 400, 0, Integer.MAX_VALUE);
		
		HEATER_FURNACE_ENGINE_ENABLED = COMMON_BUILDER.comment("Enable Induction Heater when attached to a Furnace Engine.")
				.define("heater_furnace_engine_enable", false);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Wires").push(CATAGORY_WIRES);
		
		CONNECTOR_MAX_INPUT = COMMON_BUILDER.comment("Connector max input (Energy transfer).")
				.defineInRange("connector_max_input", 2048, 0, Integer.MAX_VALUE);
		
		CONNECTOR_MAX_OUTPUT = COMMON_BUILDER.comment("Connector max output (Energy transfer).")
				.defineInRange("connector_max_output", 2048, 0, Integer.MAX_VALUE);
		
		CONNECTOR_CAPACITY = COMMON_BUILDER.comment("Connector internal capacity.")
				.defineInRange("connector_capacity", 8196, 0, Integer.MAX_VALUE);
		
		COPPER_WIRE_LOSS = COMMON_BUILDER.comment("Loss per block in copper wire (Not implemented, currently no loss).")
				.defineInRange("copper_wire_loss", 0.02d, 0d, 0.5d);
		
		GOLD_WIRE_LOSS = COMMON_BUILDER.comment("Loss per block in gold wire (Not implemented, currently no loss).")
				.defineInRange("gold_wire_loss", 0.005d, 0d, 0.5d);
		
		COMMON_BUILDER.pop();
		
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
			.sync()
			.autosave()
			.writingMode(WritingMode.REPLACE)
			.build();
		configData.load();
		spec.setConfig(configData);
	}
}
