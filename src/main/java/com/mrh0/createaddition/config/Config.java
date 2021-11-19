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
	public static final String CATAGORY_ACCUMULATOR = "accumulator";
	public static final String CATAGORY_CHARGER = "charger";
	public static final String CATAGORY_TESLA_COIL = "tesla_coil";
	public static final String CATAGORY_MISC = "misc";
	
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_RPM_RANGE;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MINIMUM_CONSUMPTION;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_CAPACITY;
	
	public static ForgeConfigSpec.IntValue FE_RPM;
	public static ForgeConfigSpec.IntValue BASELINE_STRESS;
	
	public static ForgeConfigSpec.IntValue ALTERNATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ALTERNATOR_CAPACITY;
	//public static ForgeConfigSpec.IntValue ALTERNATOR_STRESS;
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
	public static ForgeConfigSpec.IntValue CONNECTOR_MAX_LENGTH;
	
	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_CAPACITY;
	
	public static ForgeConfigSpec.IntValue CHARGER_MAX_INPUT;
	public static ForgeConfigSpec.IntValue CHARGER_CHARGE_RATE;
	public static ForgeConfigSpec.IntValue CHARGER_CAPACITY;
	
	public static ForgeConfigSpec.IntValue TESLA_COIL_MAX_INPUT;
	public static ForgeConfigSpec.IntValue TESLA_COIL_CHARGE_RATE;
	public static ForgeConfigSpec.IntValue TESLA_COIL_CAPACITY;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_ENERGY_REQUIRED;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_DMG_MOB;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_DMG_PLAYER;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_RANGE;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_EFFECT_TIME_MOB;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_EFFECT_TIME_PLAYER;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_FIRE_COOLDOWN;
	
	public static ForgeConfigSpec.DoubleValue COPPER_WIRE_LOSS;
	public static ForgeConfigSpec.DoubleValue GOLD_WIRE_LOSS;
	
	public static ForgeConfigSpec.IntValue DIAMOND_GRIT_SANDPAPER_USES;
	public static ForgeConfigSpec.IntValue OVERCHARGING_ENERGY_COST;
	public static ForgeConfigSpec.DoubleValue CERTUS_QUARTZ_CHARGE_CHANCE;
	
	public static ForgeConfigSpec.DoubleValue FURNACE_BURNER_ENGINE_SPEED;
	public static ForgeConfigSpec.DoubleValue CRUDE_BURNER_ENGINE_SPEED;
	
	static {
		
		COMMON_BUILDER.comment("General Settings").push(CATAGORY_GENERAL);
		
		FE_RPM = COMMON_BUILDER.comment("Forge Energy conversion rate (in FE/t at max RPM).")
				.defineInRange("fe_conversion", 240, 0, Integer.MAX_VALUE);
		
		BASELINE_STRESS = COMMON_BUILDER.comment("Max stress for the Alternator and Electric Motor (in SU at max RPM).")
				.defineInRange("baseline_stress", 8192, 0, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Electric Motor").push(CATAGORY_ELECTRIC_MOTOR);
		
		ELECTRIC_MOTOR_RPM_RANGE = COMMON_BUILDER.comment("Electric Motor min/max RPM.")
				.defineInRange("motor_rpm_range", 256, 1, Integer.MAX_VALUE);
		
		ELECTRIC_MOTOR_MINIMUM_CONSUMPTION = COMMON_BUILDER.comment("Electric Motor minimum required energy consumption in FE/t.")
				.defineInRange("motor_min_consumption", 8, 0, Integer.MAX_VALUE);
		
		ELECTRIC_MOTOR_MAX_INPUT = COMMON_BUILDER.comment("Electric Motor max input in FE (Energy transfer not consumption).")
				.defineInRange("motor_max_input", 256, 0, Integer.MAX_VALUE);
		
		ELECTRIC_MOTOR_CAPACITY = COMMON_BUILDER.comment("Electric Motor internal capacity in FE.")
				.defineInRange("motor_capacity", 2048, 0, Integer.MAX_VALUE);
		
		//ELECTRIC_MOTOR_STRESS = COMMON_BUILDER.comment("Electric Motor generated stress at max RPM in SU.")
		//		.defineInRange("motor_stress", 4096, 0, Integer.MAX_VALUE);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Alternator").push(CATAGORY_ALTERNATOR);
		
		ALTERNATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Alternator max input in FE (Energy transfer, not generation).")
				.defineInRange("generator_max_output", 256, 0, Integer.MAX_VALUE);
		
		ALTERNATOR_CAPACITY = COMMON_BUILDER.comment("Alternator internal capacity in FE.")
				.defineInRange("generator_capacity", 2048, 0, Integer.MAX_VALUE);
		
		//ALTERNATOR_STRESS = COMMON_BUILDER.comment("Alternator base stress impact.")
		//		.defineInRange("generator_stress", 16, 0, Integer.MAX_VALUE);
		
		ALTERNATOR_EFFICIENCY = COMMON_BUILDER.comment("Alternator efficiency relative to base conversion rate.")
				.defineInRange("generator_efficiency", 0.75d, 0.01d, 1.0d);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Rolling Mill").push(CATAGORY_ROLLING_MILL);
		
		ROLLING_MILL_PROCESSING_DURATION = COMMON_BUILDER.comment("Rolling Mill duration in ticks.")
				.defineInRange("rolling_mill_processing_duration", 100, 0, Integer.MAX_VALUE);
		
		ROLLING_MILL_STRESS = COMMON_BUILDER.comment("Rolling Mill base stress impact.")
				.defineInRange("rolling_mill_stress", 16, 0, 1024);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Heater").push(CATAGORY_HEATER);
		
		HEATER_MAX_INPUT = COMMON_BUILDER.comment("Induction Heater max input in FE (Energy transfer, not consumption).")
				.defineInRange("heater_max_input", 256, 0, Integer.MAX_VALUE);
		
		HEATER_CAPACITY = COMMON_BUILDER.comment("Induction Heater internal capacity in FE.")
				.defineInRange("heater_capacity", 2048, 0, Integer.MAX_VALUE);
		
		HEATER_NORMAL_CONSUMPTION = COMMON_BUILDER.comment("Induction Heater normal consumption rate in FE/t.")
				.defineInRange("heater_normal_consumption", 256, 0, Integer.MAX_VALUE);
		
		HEATER_FURNACE_ENGINE_CONSUMPTION = COMMON_BUILDER.comment("Induction Heater when attached to a Furnace Engine consumption rate in FE/t.")
				.defineInRange("heater_furnace_engine_consumption", 1024, 0, Integer.MAX_VALUE);
		
		HEATER_FURNACE_ENGINE_ENABLED = COMMON_BUILDER.comment("Enable Induction Heater when attached to a Furnace Engine.")
				.define("heater_furnace_engine_enable", false);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Wires").push(CATAGORY_WIRES);
		
		CONNECTOR_MAX_INPUT = COMMON_BUILDER.comment("Connector max input in FE/t (Energy transfer).")
				.defineInRange("connector_max_input", 256, 0, Integer.MAX_VALUE);
		
		CONNECTOR_MAX_OUTPUT = COMMON_BUILDER.comment("Connector max output in FE/t (Energy transfer).")
				.defineInRange("connector_max_output", 256, 0, Integer.MAX_VALUE);
		
		CONNECTOR_CAPACITY = COMMON_BUILDER.comment("Connector internal capacity in FE.")
				.defineInRange("connector_capacity", 512, 0, Integer.MAX_VALUE);
		
		CONNECTOR_MAX_LENGTH = COMMON_BUILDER.comment("Max wire length in blocks.")
				.defineInRange("connector_capacity", 12, 0, 256);
		
		/*COPPER_WIRE_LOSS = COMMON_BUILDER.comment("Loss per block in copper wire (Not implemented, currently no loss).")
				.defineInRange("copper_wire_loss", 0.02d, 0d, 0.5d);
		
		GOLD_WIRE_LOSS = COMMON_BUILDER.comment("Loss per block in gold wire (Not implemented, currently no loss).")
				.defineInRange("gold_wire_loss", 0.005d, 0d, 0.5d);*/
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Accumulator").push(CATAGORY_ACCUMULATOR);
		
		ACCUMULATOR_MAX_INPUT = COMMON_BUILDER.comment("Accumulator max input in FE/t (Energy transfer).")
				.defineInRange("accumulator_max_input", 512, 0, Integer.MAX_VALUE);
		
		ACCUMULATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Accumulator max output in FE/t (Energy transfer).")
				.defineInRange("accumulator_max_output", 512, 0, Integer.MAX_VALUE);
		
		ACCUMULATOR_CAPACITY = COMMON_BUILDER.comment("Accumulator internal capacity in FE.")
				.defineInRange("accumulator_capacity", 4196000, 0, Integer.MAX_VALUE);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Charger (Deprecated)").push(CATAGORY_CHARGER);
		
		CHARGER_MAX_INPUT = COMMON_BUILDER.comment("Charger max input in FE/t (Energy transfer).")
				.defineInRange("charger_max_input", 8192, 0, Integer.MAX_VALUE);
		
		CHARGER_CHARGE_RATE = COMMON_BUILDER.comment("Charger charge rate in FE/t.")
				.defineInRange("charger_charge_rate", 8192, 0, Integer.MAX_VALUE);
		
		CHARGER_CAPACITY = COMMON_BUILDER.comment("Charger internal capacity in FE.")
				.defineInRange("charger_capacity", 32000, 0, Integer.MAX_VALUE);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Tesla Coil").push(CATAGORY_TESLA_COIL);
		
		TESLA_COIL_MAX_INPUT = COMMON_BUILDER.comment("Charger max input in FE/t (Energy transfer).")
				.defineInRange("tesla_coil_max_input", 8192, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_CHARGE_RATE = COMMON_BUILDER.comment("Charger charge rate in FE/t.")
				.defineInRange("tesla_coil_charge_rate", 8192, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_CAPACITY = COMMON_BUILDER.comment("Charger internal capacity in FE.")
				.defineInRange("tesla_coil_capacity", 32000, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_ENERGY_REQUIRED = COMMON_BUILDER.comment("Energy consumed when Tesla Coil is fired (in FE).")
				.defineInRange("tesla_coil_hurt_energy_required", 1024, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_RANGE = COMMON_BUILDER.comment("Hurt range (in blocks/meters).")
				.defineInRange("tesla_coil_hurt_range", 4, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_DMG_MOB = COMMON_BUILDER.comment("Damaged dealt to mobs when Tesla Coil is fired (in half hearts).")
				.defineInRange("tesla_coil_hurt_mob", 3, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_EFFECT_TIME_MOB = COMMON_BUILDER.comment("The duration of the Shocked effect for mobs (in ticks).")
				.defineInRange("tesla_coil_effect_time_mob", 20, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_DMG_PLAYER = COMMON_BUILDER.comment("Damaged dealt to players when Tesla Coil is fired (in half hearts).")
				.defineInRange("tesla_coil_hurt_player", 2, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_EFFECT_TIME_PLAYER = COMMON_BUILDER.comment("The duration of the Shocked effect for players (in ticks).")
				.defineInRange("tesla_coil_effect_time_player", 20, 0, Integer.MAX_VALUE);
		
		TESLA_COIL_HURT_FIRE_COOLDOWN = COMMON_BUILDER.comment("Tesla Coil fire interval (in ticks).")
				.defineInRange("tesla_coil_fire_cooldown", 20, 0, Integer.MAX_VALUE);
		
		COMMON_BUILDER.pop();
		
		
		COMMON_BUILDER.comment("Misc").push(CATAGORY_MISC);
		
		DIAMOND_GRIT_SANDPAPER_USES = COMMON_BUILDER.comment("Diamond Grit Sandpaper durability (number of uses).")
				.defineInRange("diamond_grit_sandpaper_uses", 1024, 3, Integer.MAX_VALUE);
		
		OVERCHARGING_ENERGY_COST = COMMON_BUILDER.comment("The energy cost of turning Chromatic Compound into Overcharged Alloy.")
				.defineInRange("overcharging_energy_cost", 16777216, 1, Integer.MAX_VALUE);
		
		CERTUS_QUARTZ_CHARGE_CHANCE = COMMON_BUILDER.comment("The chance that a AE2 Certus Quartz will be turned into Charged Certus Quartz for every tick.")
				.defineInRange("certus_quartz_charge_chance", 0.96d, 0d, 1d);
		
		FURNACE_BURNER_ENGINE_SPEED = COMMON_BUILDER.comment("The relative speed of the Furnace Engine when powered by a Furnace Burner.")
				.defineInRange("furnace_burner_engine_speed", 1d, 0d, 100d);
		
		CRUDE_BURNER_ENGINE_SPEED = COMMON_BUILDER.comment("The relative speed of the Furnace Engine when powered by a Crude Burner.")
				.defineInRange("crude_burner_engine_speed", 2d, 0d, 100d);
		
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
