package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorBlock;
import com.mrh0.createaddition.blocks.alternator.AlternatorBlock;
import com.mrh0.createaddition.blocks.cake.Cake;
import com.mrh0.createaddition.blocks.connector.ConnectorBlock;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlock;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurner;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelay;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlock;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoil;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.components.AssemblyOperatorBlockItem;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.AllTags.pickaxeOnly;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;


@SuppressWarnings("CommentedOutCode")
public class CABlocks {
	
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.creativeModeTab(() -> ModGroup.MAIN);
	public static final BlockEntry<LiquidBlazeBurner> LIQUID_BLAZE_BURNER = REGISTRATE.block("liquid_blaze_burner",  LiquidBlazeBurner::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.color(MaterialColor.COLOR_GRAY))
			.properties(p -> p.lightLevel(BlazeBurnerBlock::getLight))
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			//.tag(AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.PASSIVE_BOILER_HEATERS.tag)
			.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
			//.onRegister(movementBehaviour(new BlazeBurnerMovementBehaviour()))
			//.onRegister(interactionBehaviour(new BlazeBurnerInteractionBehaviour()))
			.register();
	
	public static final BlockEntry<ElectricMotorBlock> ELECTRIC_MOTOR = REGISTRATE.block("electric_motor", ElectricMotorBlock::new)
			.initialProperties(SharedProperties::stone)
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.transform(BlockStressDefaults.setCapacity(Config.BASELINE_STRESS.get()/256.0))
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<AlternatorBlock> ALTERNATOR = REGISTRATE.block("alternator", AlternatorBlock::new)
			.initialProperties(SharedProperties::stone)
			.transform(BlockStressDefaults.setImpact(Config.BASELINE_STRESS.get()/256.0))
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
	
	public static final BlockEntry<ConnectorBlock> CONNECTOR_COPPER = REGISTRATE.block("connector",  ConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();

	/*public static final BlockEntry<ConnectorBlock> CONNECTOR_GOLD = REGISTRATE.block("connector_gold",  ConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();*/

	public static final BlockEntry<AccumulatorBlock> ACCUMULATOR = REGISTRATE.block("accumulator",  AccumulatorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<RedstoneRelay> REDSTONE_RELAY = REGISTRATE.block("redstone_relay",  RedstoneRelay::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<Cake> CHOCOLATE_CAKE = REGISTRATE.block("chocolate_cake",  Cake::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<Cake> HONEY_CAKE = REGISTRATE.block("honey_cake",  Cake::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();
	
	public static final BlockEntry<TeslaCoil> TESLA_COIL = REGISTRATE.block("tesla_coil",  TeslaCoil::new)
			.initialProperties(SharedProperties::stone)
			.item(AssemblyOperatorBlockItem::new)
			.transform(customItemModel())
			.register();

	/*public static final BlockEntry<CasingBlock> OVERCHARGED_CASING =
			REGISTRATE.block("overcharged_casing", CasingBlock::new)
				.transform(BuilderTransformers.casing(CASpriteShifts.OVERCHARGED_CASING))
				.properties(p -> p.lightLevel($ -> 4))
				.register();*/
	
	public static void register() {
		Create.REGISTRATE.addToSection(TESLA_COIL, AllSections.KINETICS);
		Create.REGISTRATE.addToSection(CREATIVE_ENERGY, AllSections.KINETICS);
		Create.REGISTRATE.addToSection(CONNECTOR_COPPER, AllSections.KINETICS);
		Create.REGISTRATE.addToSection(ACCUMULATOR, AllSections.KINETICS);
		Create.REGISTRATE.addToSection(REDSTONE_RELAY, AllSections.KINETICS);
	}
}