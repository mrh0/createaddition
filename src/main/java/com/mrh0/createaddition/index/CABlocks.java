package com.mrh0.createaddition.index;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorBlock;
import com.mrh0.createaddition.blocks.alternator.AlternatorBlock;
import com.mrh0.createaddition.blocks.cake.Cake;
import com.mrh0.createaddition.blocks.charger.Charger;
import com.mrh0.createaddition.blocks.connector.ConnectorBlock;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlock;
import com.mrh0.createaddition.blocks.crude_burner.CrudeBurner;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.furnace_burner.FurnaceBurner;
import com.mrh0.createaddition.blocks.heater.HeaterBlock;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelay;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlock;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.Create;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class CABlocks {
	
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.itemGroup(() -> ModGroup.MAIN);
	
	public static final BlockEntry<ElectricMotorBlock> ELECTRIC_MOTOR = REGISTRATE.block("electric_motor", ElectricMotorBlock::new)
			.initialProperties(SharedProperties::stone)
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.transform(BlockStressDefaults.setCapacity(Config.BASELINE_STRESS.get()/256))
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<AlternatorBlock> ALTERNATOR = REGISTRATE.block("alternator", AlternatorBlock::new)
			.initialProperties(SharedProperties::stone)
			.transform(BlockStressDefaults.setImpact(Config.BASELINE_STRESS.get()/256))
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<RollingMillBlock> ROLLING_MILL = REGISTRATE.block("rolling_mill", RollingMillBlock::new)
			.initialProperties(SharedProperties::stone)
			.transform(BlockStressDefaults.setImpact(Config.ROLLING_MILL_STRESS.get()))
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<CreativeEnergyBlock> CREATIVE_ENERGY = REGISTRATE.block("creative_energy", CreativeEnergyBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<ConnectorBlock> CONNECTOR = REGISTRATE.block("connector",  ConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<AccumulatorBlock> ACCUMULATOR = REGISTRATE.block("accumulator",  AccumulatorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<HeaterBlock> HEATER = REGISTRATE.block("heater",  HeaterBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<RedstoneRelay> REDSTONE_RELAY = REGISTRATE.block("redstone_relay",  RedstoneRelay::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<FurnaceBurner> FURNACE_BURNER = REGISTRATE.block("furnace_burner",  FurnaceBurner::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<CrudeBurner> CRUDE_BURNER = REGISTRATE.block("crude_burner",  CrudeBurner::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<Charger> CHARGER = REGISTRATE.block("charger",  Charger::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<Cake> CHOCOLATE_CAKE = REGISTRATE.block("chocolate_cake",  Cake::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).harvestLevel(0).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<Cake> HONEY_CAKE = REGISTRATE.block("honey_cake",  Cake::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).harvestLevel(0).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();
	
	public static void register() {
		//Create.registrate().addToSection(ELECTRIC_MOTOR, AllSections.KINETICS);
		//Create.registrate().addToSection(ALTERNATOR, AllSections.KINETICS);
		//Create.registrate().addToSection(ROLLING_MILL, AllSections.KINETICS);
		Create.registrate().addToSection(CHARGER, AllSections.KINETICS);
		Create.registrate().addToSection(FURNACE_BURNER, AllSections.KINETICS);
		Create.registrate().addToSection(CRUDE_BURNER, AllSections.KINETICS);
		Create.registrate().addToSection(CREATIVE_ENERGY, AllSections.KINETICS);
		Create.registrate().addToSection(CONNECTOR, AllSections.KINETICS);
		Create.registrate().addToSection(ACCUMULATOR, AllSections.KINETICS);
		//Create.registrate().addToSection(HEATER, AllSections.KINETICS);
		Create.registrate().addToSection(REDSTONE_RELAY, AllSections.KINETICS);
	}
}
