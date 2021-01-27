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
	
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	
	public static ForgeConfigSpec COMMON_CONFIG;
	
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MIN_RPM;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_RPM;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_MAX_INPUT;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_CAPACITY;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_CONSUMPTION;
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_STRESS;
	
	public static ForgeConfigSpec.IntValue ALTERNATOR_MAX_OUTPUT;
	public static ForgeConfigSpec.IntValue ALTERNATOR_CAPACITY;
	public static ForgeConfigSpec.IntValue ALTERNATOR_STRESS;
	
	public static ForgeConfigSpec.IntValue ROLLING_MILL_PROCESSING_DURATION;
	public static ForgeConfigSpec.IntValue ROLLING_MILL_STRESS;
	
	static {
		
		COMMON_BUILDER.comment("General Settings").push(CATAGORY_GENERAL);

		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Electric Motor").push(CATAGORY_ELECTRIC_MOTOR);
		
		ELECTRIC_MOTOR_MIN_RPM = COMMON_BUILDER.comment("Electric Motor minimum RPM.")
				.defineInRange("motor_min_rpm", 32, 0, 256);
		
		ELECTRIC_MOTOR_MAX_RPM = COMMON_BUILDER.comment("Electric Motor maximum RPM.")
				.defineInRange("motor_max_rpm", 256, 1, 256);
		
		ELECTRIC_MOTOR_MAX_INPUT = COMMON_BUILDER.comment("Electric Motor max input in FE.")
				.defineInRange("motor_max_input", 8192, 0, Short.MAX_VALUE);
		
		ELECTRIC_MOTOR_CAPACITY = COMMON_BUILDER.comment("Electric Motor internal capacity in FE.")
				.defineInRange("motor_capacity", 16000, 0, Short.MAX_VALUE);
		
		ELECTRIC_MOTOR_CONSUMPTION = COMMON_BUILDER.comment("Electric Motor consumption in FE/t at max RPM.")
				.defineInRange("motor_consumption", 60, 0, Short.MAX_VALUE);
		
		ELECTRIC_MOTOR_STRESS = COMMON_BUILDER.comment("Electric Motor generated base stress (Not implemented).")
				.defineInRange("motor_stress", 16, 0, 256);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Alternator").push(CATAGORY_ALTERNATOR);
		
		ALTERNATOR_MAX_OUTPUT = COMMON_BUILDER.comment("Alternator max input in FE.")
				.defineInRange("generator_max_output", 8192, 0, Short.MAX_VALUE);
		
		ALTERNATOR_CAPACITY = COMMON_BUILDER.comment("Alternator internal capacity in FE.")
				.defineInRange("generator_capacity", 32000, 0, Short.MAX_VALUE);
		
		ALTERNATOR_STRESS = COMMON_BUILDER.comment("Alternator base stress impact.")
				.defineInRange("generator_consumption", 16, 0, 1024);
		
		COMMON_BUILDER.pop();
		
		COMMON_BUILDER.comment("Rolling Mill").push(CATAGORY_ROLLING_MILL);
		
		ROLLING_MILL_PROCESSING_DURATION = COMMON_BUILDER.comment("Rolling Mill duration in ticks.")
				.defineInRange("rolling_mill_processing_duration", 100, 0, Short.MAX_VALUE);
		
		ROLLING_MILL_STRESS = COMMON_BUILDER.comment("Rolling Mill base stress impact.")
				.defineInRange("rolling_mill_stress", 16, 0, 1024);
		
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
