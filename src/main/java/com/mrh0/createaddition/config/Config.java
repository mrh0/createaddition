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
	public static final String CATAGORY_WIRES = "wires";
	public static final String CATAGORY_ACCUMULATOR = "accumulator";
	public static final String CATAGORY_PEI = "portable_energy_interface";
	public static final String CATAGORY_TESLA_COIL = "tesla_coil";
	public static final String CATAGORY_MISC = "misc";

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_RPM_RANGE;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MINIMUM_CONSUMPTION;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_CAPACITY;

	public static ForgeConfigSpec.IntValue FE_RPM;
	public static ForgeConfigSpec.IntValue MAX_STRESS;

	public static ForgeConfigSpec.IntValue ALTERNATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ALTERNATOR_CAPACITY;
	public static ForgeConfigSpec.DoubleValue ALTERNATOR_EFFICIENCY;

	public static ForgeConfigSpec.IntValue ROLLING_MILL_PROCESSING_DURATION;
	public static ForgeConfigSpec.IntValue ROLLING_MILL_STRESS;

	public static ForgeConfigSpec.IntValue SMALL_CONNECTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue SMALL_CONNECTOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue SMALL_CONNECTOR_MAX_LENGTH;

	public static ForgeConfigSpec.IntValue SMALL_LIGHT_CONNECTOR_CONSUMPTION;

	public static ForgeConfigSpec.IntValue LARGE_CONNECTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue LARGE_CONNECTOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue LARGE_CONNECTOR_MAX_LENGTH;

	public static ForgeConfigSpec.BooleanValue CONNECTOR_IGNORE_FACE_CHECK;
	public static ForgeConfigSpec.BooleanValue CONNECTOR_ALLOW_PASSIVE_IO;

	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_CAPACITY;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_HEIGHT;
	public static ForgeConfigSpec.IntValue ACCUMULATOR_MAX_WIDTH;

	public static ForgeConfigSpec.IntValue PEI_MAX_INPUT;
	public static ForgeConfigSpec.IntValue PEI_MAX_OUTPUT;

	public static ForgeConfigSpec.IntValue TESLA_COIL_MAX_INPUT;
	public static ForgeConfigSpec.IntValue TESLA_COIL_CHARGE_RATE;
	public static ForgeConfigSpec.IntValue TESLA_COIL_RECIPE_CHARGE_RATE;
	public static ForgeConfigSpec.IntValue TESLA_COIL_CAPACITY;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_ENERGY_REQUIRED;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_DMG_MOB;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_DMG_PLAYER;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_RANGE;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_EFFECT_TIME_MOB;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_EFFECT_TIME_PLAYER;
	public static ForgeConfigSpec.IntValue TESLA_COIL_HURT_FIRE_COOLDOWN;

	public static ForgeConfigSpec.IntValue DIAMOND_GRIT_SANDPAPER_USES;
	public static ForgeConfigSpec.DoubleValue BARBED_WIRE_DAMAGE;

	static {
		COMMON_BUILDER.comment("Make sure config changes are duplicated on both Clients and the Server when running a dedicated Server,")
					.comment(" as the config isnt synced between Clients and Server.");
		COMMON_BUILDER.comment("General Settings").push(CATAGORY_GENERAL);
		FE_RPM = COMMON_BUILDER.comment("Forge Energy conversion rate (in FE/t at 256 RPM, value is the FE/t generated and consumed is at 256rpm).")
				.defineInRange("fe_at_max_rpm", 480, 0, Integer.MAX_VALUE);

		MAX_STRESS = COMMON_BUILDER.comment("Max stress for the Alternator and Electric Motor (in SU at 256 RPM).")
				.defineInRange("max_stress", 16384, 0, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Electric Motor").push(CATAGORY_ELECTRIC_MOTOR);
		ELECTRIC_MOTOR_RPM_RANGE = COMMON_BUILDER.comment("Electric Motor min/max RPM.")
				.defineInRange("motor_rpm_range", 256, 1, Integer.MAX_VALUE);

		ELECTRIC_MOTOR_MINIMUM_CONSUMPTION = COMMON_BUILDER.comment("Electric Motor minimum required energy consumption in FE/t.")
				.defineInRange("motor_min_consumption", 8, 0, Integer.MAX_VALUE);

		ELECTRIC_MOTOR_MAX_INPUT = COMMON_BUILDER.comment("Electric Motor max input in FE (Energy transfer not consumption).")
				.defineInRange("motor_max_input", 5000, 0, Integer.MAX_VALUE);

		ELECTRIC_MOTOR_CAPACITY = COMMON_BUILDER.comment("Electric Motor internal capacity in FE.")
				.defineInRange("motor_capacity", 5000, 0, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Alternator").push(CATAGORY_ALTERNATOR);
		ALTERNATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Alternator max input in FE (Energy transfer, not generation).")
				.defineInRange("generator_max_output", 5000, 0, Integer.MAX_VALUE);

		ALTERNATOR_CAPACITY = COMMON_BUILDER.comment("Alternator internal capacity in FE.")
				.defineInRange("generator_capacity", 5000, 0, Integer.MAX_VALUE);

		ALTERNATOR_EFFICIENCY = COMMON_BUILDER.comment("Alternator efficiency relative to base conversion rate.")
				.defineInRange("generator_efficiency", 0.75d, 0.01d, 1.0d);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Rolling Mill").push(CATAGORY_ROLLING_MILL);
		ROLLING_MILL_PROCESSING_DURATION = COMMON_BUILDER.comment("Rolling Mill duration in ticks.")
				.defineInRange("rolling_mill_processing_duration", 100, 0, Integer.MAX_VALUE);

		ROLLING_MILL_STRESS = COMMON_BUILDER.comment("Rolling Mill base stress impact.")
				.defineInRange("rolling_mill_stress", 16, 0, 1024);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Wires").push(CATAGORY_WIRES);
		SMALL_CONNECTOR_MAX_INPUT = COMMON_BUILDER.comment("Small Connector max input in FE/t (Energy transfer).")
				.defineInRange("small_connector_max_input", 1000, 0, Integer.MAX_VALUE);

		SMALL_CONNECTOR_MAX_OUTPUT = COMMON_BUILDER.comment("Small Connector max output in FE/t (Energy transfer).")
				.defineInRange("small_connector_max_output", 1000, 0, Integer.MAX_VALUE);

		SMALL_CONNECTOR_MAX_LENGTH = COMMON_BUILDER.comment("Small Connector max wire length in blocks.")
				.defineInRange("small_connector_wire_length", 16, 0, 256);

		SMALL_LIGHT_CONNECTOR_CONSUMPTION = COMMON_BUILDER.comment("Small Connector With Light energy consumption in FE/t.")
				.defineInRange("small_light_connector_consumption", 1, 0, Integer.MAX_VALUE);


		LARGE_CONNECTOR_MAX_INPUT = COMMON_BUILDER.comment("Large Connector max input in FE/t (Energy transfer).")
				.defineInRange("large_connector_max_input", 5000, 0, Integer.MAX_VALUE);

		LARGE_CONNECTOR_MAX_OUTPUT = COMMON_BUILDER.comment("Large Connector max output in FE/t (Energy transfer).")
				.defineInRange("large_connector_max_output", 5000, 0, Integer.MAX_VALUE);

		LARGE_CONNECTOR_MAX_LENGTH = COMMON_BUILDER.comment("Large Connector max wire length in blocks.")
				.defineInRange("large_connector_wire_length", 32, 0, 256);


		CONNECTOR_IGNORE_FACE_CHECK = COMMON_BUILDER.comment("Ignore checking if block face can support connector.")
				.define("connector_ignore_face_check", true);

		CONNECTOR_ALLOW_PASSIVE_IO = COMMON_BUILDER.comment("Allows blocks attached to a connector to freely pass energy to and from the connector network.")
				.define("connector_allow_passive_io", true);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Accumulator").push(CATAGORY_ACCUMULATOR);
		ACCUMULATOR_MAX_INPUT = COMMON_BUILDER.comment("Accumulator max input in FE/t (Energy transfer).")
				.defineInRange("accumulator_max_input", 5000, 0, Integer.MAX_VALUE);

		ACCUMULATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Accumulator max output in FE/t (Energy transfer).")
				.defineInRange("accumulator_max_output", 5000, 0, Integer.MAX_VALUE);

		ACCUMULATOR_CAPACITY = COMMON_BUILDER.comment("Accumulator internal capacity per block in FE.")
				.defineInRange("accumulator_capacity", 2_000_000, 0, Integer.MAX_VALUE);

		ACCUMULATOR_MAX_HEIGHT = COMMON_BUILDER.comment("Accumulator max multiblock height.")
				.defineInRange("accumulator_max_height", 5, 1, 8);

		ACCUMULATOR_MAX_WIDTH = COMMON_BUILDER.comment("Accumulator max multiblock width.")
				.defineInRange("accumulator_max_width", 3, 1, 8);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.comment("Portable Energy Interface").push(CATAGORY_PEI);
		PEI_MAX_INPUT = COMMON_BUILDER.comment("PEI max input in FE/t (Energy transfer).")
				.defineInRange("pei_max_input", 5000, 0, Integer.MAX_VALUE);

		PEI_MAX_OUTPUT = COMMON_BUILDER.comment("PEI max output in FE/t (Energy transfer).")
				.defineInRange("pei_max_output", 5000, 0, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Tesla Coil").push(CATAGORY_TESLA_COIL);
		TESLA_COIL_MAX_INPUT = COMMON_BUILDER.comment("Tesla Coil max input in FE/t (Energy transfer).")
				.defineInRange("tesla_coil_max_input", 10000, 0, Integer.MAX_VALUE);

		TESLA_COIL_CHARGE_RATE = COMMON_BUILDER.comment("Tesla Coil charge rate in FE/t.")
				.defineInRange("tesla_coil_charge_rate", 5000, 0, Integer.MAX_VALUE);

		TESLA_COIL_RECIPE_CHARGE_RATE = COMMON_BUILDER.comment("Tesla Coil charge rate in FE/t for recipes.")
				.defineInRange("tesla_coil_recipe_charge_rate", 2000, 0, Integer.MAX_VALUE);

		TESLA_COIL_CAPACITY = COMMON_BUILDER.comment("Tesla Coil internal capacity in FE.")
				.defineInRange("tesla_coil_capacity", 40_000, 0, Integer.MAX_VALUE);

		TESLA_COIL_HURT_ENERGY_REQUIRED = COMMON_BUILDER.comment("Energy consumed when Tesla Coil is fired (in FE).")
				.defineInRange("tesla_coil_hurt_energy_required", 1000, 0, Integer.MAX_VALUE);

		TESLA_COIL_HURT_RANGE = COMMON_BUILDER.comment("Hurt range (in blocks/meters).")
				.defineInRange("tesla_coil_hurt_range", 3, 0, Integer.MAX_VALUE);

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

		BARBED_WIRE_DAMAGE = COMMON_BUILDER.comment("Barbed Wire Damage.")
				.defineInRange("barbed_wire_damage", 2, 0, Float.MAX_VALUE);

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
