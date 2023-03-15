package com.mrh0.createaddition.index;

import static com.simibubi.create.AllTags.pickaxeOnly;
import static com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.accumulator.AccumulatorBlock;
import com.mrh0.createaddition.blocks.alternator.AlternatorBlock;
import com.mrh0.createaddition.blocks.barbed_wire.BarbedWireBlock;
import com.mrh0.createaddition.blocks.cake.CACakeBlock;
import com.mrh0.createaddition.blocks.connector.ConnectorBlock;
import com.mrh0.createaddition.blocks.modular_accumulator.*;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceMovement;
import com.mrh0.createaddition.energy.NodeMovementBehaviour;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlock;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlock;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlock;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.components.AssemblyOperatorBlockItem;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CABlocks {

	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.creativeModeTab(() -> ModGroup.MAIN);

	public static final BlockEntry<ElectricMotorBlock> ELECTRIC_MOTOR = REGISTRATE.block("electric_motor", ElectricMotorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.transform(BlockStressDefaults.setCapacity(Config.BASELINE_STRESS.get()/256))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<AlternatorBlock> ALTERNATOR = REGISTRATE.block("alternator", AlternatorBlock::new)
			.initialProperties(SharedProperties::softMetal)
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
			.initialProperties(SharedProperties::softMetal)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<ConnectorBlock> CONNECTOR_COPPER = REGISTRATE.block("connector",  ConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(AllMovementBehaviours.movementBehaviour(new NodeMovementBehaviour()))
			.item()
			.transform(customItemModel())
			.register();

	/*public static final BlockEntry<ConnectorBlock> CONNECTOR_GOLD = REGISTRATE.block("connector_gold",  ConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.item()
			.transform(customItemModel())
			.register();*/

	public static final BlockEntry<AccumulatorBlock> ACCUMULATOR = REGISTRATE.block("accumulator",  AccumulatorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.onRegister(AllMovementBehaviours.movementBehaviour(new NodeMovementBehaviour()))
			.item()
			.tab(() -> null)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<RedstoneRelayBlock> REDSTONE_RELAY = REGISTRATE.block("redstone_relay",  RedstoneRelayBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(AllMovementBehaviours.movementBehaviour(new NodeMovementBehaviour()))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<CACakeBlock> CHOCOLATE_CAKE = REGISTRATE.block("chocolate_cake",  CACakeBlock::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<CACakeBlock> HONEY_CAKE = REGISTRATE.block("honey_cake",  CACakeBlock::new)
			.initialProperties(Material.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<BarbedWireBlock> BARBED_WIRE = REGISTRATE.block("barbed_wire",  BarbedWireBlock::new)
			.initialProperties(Material.WEB)
			.properties(props -> props.noCollission().requiresCorrectToolForDrops().strength(4.0F))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<TeslaCoilBlock> TESLA_COIL = REGISTRATE.block("tesla_coil",  TeslaCoilBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.item(AssemblyOperatorBlockItem::new)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<ModularAccumulatorBlock> MODULAR_ACCUMULATOR = REGISTRATE.block("modular_accumulator",  ModularAccumulatorBlock::regular)
			.initialProperties(SharedProperties::softMetal)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.onRegister(AllMovementBehaviours.movementBehaviour(new ModularAccumulatorMovement()))
			.onRegister(connectedTextures(ModularAccumulatorCTBehaviour::new))
			.onRegister(assignDataBehaviour(new ModularAccumulatorDisplaySource(), "modular_accumulator"))
			//.onRegister(assignDataBehaviour(ForgeEnergyDisplaySource.INSTANCE, "forge_energy"))
			.addLayer(() -> RenderType::cutoutMipped)
			.item(ModularAccumulatorBlockItem::new)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<LiquidBlazeBurnerBlock> LIQUID_BLAZE_BURNER = REGISTRATE.block("liquid_blaze_burner",  LiquidBlazeBurnerBlock::new)
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

	public static final BlockEntry<PortableEnergyInterfaceBlock> PORTABLE_ENERGY_INTERFACE = REGISTRATE.block("portable_energy_interface",  PortableEnergyInterfaceBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.onRegister(AllMovementBehaviours.movementBehaviour(new PortableEnergyInterfaceMovement()))
			.addLayer(() -> RenderType::cutoutMipped)
			.item()
			.transform(customItemModel())
			.register();

	/*public static final BlockEntry<CasingBlock> COPPER_WIRE_CASING = REGISTRATE.block("copper_wire_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.PODZOL))
			.transform(BuilderTransformers.casing(() -> CASpriteShifts.COPPER_WIRE_CASING))
			.register();*/

	public static void register() {
		REGISTRATE.addToSection(TESLA_COIL, AllSections.KINETICS);
		REGISTRATE.addToSection(CREATIVE_ENERGY, AllSections.KINETICS);
		REGISTRATE.addToSection(CONNECTOR_COPPER, AllSections.KINETICS);
		REGISTRATE.addToSection(ACCUMULATOR, AllSections.KINETICS);
		REGISTRATE.addToSection(REDSTONE_RELAY, AllSections.KINETICS);
		REGISTRATE.addToSection(BARBED_WIRE, AllSections.CURIOSITIES);
		REGISTRATE.addToSection(MODULAR_ACCUMULATOR, AllSections.KINETICS);
		REGISTRATE.addToSection(PORTABLE_ENERGY_INTERFACE, AllSections.KINETICS);
	}
}
