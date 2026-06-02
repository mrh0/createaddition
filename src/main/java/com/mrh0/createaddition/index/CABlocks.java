package com.mrh0.createaddition.index;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.alternator.AlternatorBlock;
import com.mrh0.createaddition.blocks.barbed_wire.BarbedWireBlock;
import com.mrh0.createaddition.blocks.cake.CACakeBlock;
import com.mrh0.createaddition.blocks.connector.LargeConnectorBlock;
import com.mrh0.createaddition.blocks.connector.SmallConnectorBlock;
import com.mrh0.createaddition.blocks.connector.SmallLightConnectorBlock;
import com.mrh0.createaddition.blocks.digital_adapter.DigitalAdapterBlock;
import com.mrh0.createaddition.blocks.modular_accumulator.*;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyInterfaceMovement;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.datagen.Models.BlockGenHelper;
import com.mrh0.createaddition.energy.NodeMovementBehaviour;
import com.mrh0.createaddition.blocks.creative_energy.CreativeEnergyBlock;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlock;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.redstone_relay.RedstoneRelayBlock;
import com.mrh0.createaddition.blocks.rolling_mill.RollingMillBlock;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.item.BiomassPelletBlockItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.BlockEntry;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

@SuppressWarnings("removal")
public class CABlocks {

	static {
		CreateAddition.REGISTRATE.setCreativeTab(CreateAddition.MAIN_TAB);
	}

	public static final BlockEntry<ElectricMotorBlock> ELECTRIC_MOTOR = CreateAddition.REGISTRATE
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
            .block("electric_motor", ElectricMotorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.transform(pickaxeOnly())
			.blockstate(BlockGenHelper.directionalBlockState())
            .onRegister(BlockStressValues.setGeneratorSpeed(256, true))
            .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, () -> CommonConfig.MAX_STRESS.get()/256f))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<AlternatorBlock> ALTERNATOR = CreateAddition.REGISTRATE
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
            .block("alternator", AlternatorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.transform(pickaxeOnly())
		    .blockstate(BlockGenHelper.directionalBlockState())
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, () -> CommonConfig.MAX_STRESS.get()/256f))
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<RollingMillBlock> ROLLING_MILL = CreateAddition.REGISTRATE
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
            .block("rolling_mill", RollingMillBlock::new)
			.initialProperties(SharedProperties::stone)
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, () -> CommonConfig.ROLLING_MILL_STRESS.get()))
			.transform(axeOrPickaxe())
			.blockstate(BlockGenHelper.horizontalBlockState())
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<CreativeEnergyBlock> CREATIVE_ENERGY = CreateAddition.REGISTRATE.block("creative_energy", CreativeEnergyBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.blockstate(BlockGenHelper.simpleBlock())
			.item()
			.properties(p -> p.rarity(Rarity.EPIC))
			.transform(customItemModel())
			.register();

	public static final BlockEntry<SmallConnectorBlock> SMALL_CONNECTOR = CreateAddition.REGISTRATE.block("connector",  SmallConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(movementBehaviour(new NodeMovementBehaviour()))
			.blockstate(SmallConnectorBlock::makeBlockState)
			.item()
			.transform(customItemModel("connector","small","item"))
			.register();

	public static final BlockEntry<SmallLightConnectorBlock> SMALL_LIGHT_CONNECTOR = CreateAddition.REGISTRATE.block("small_light_connector",  SmallLightConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(movementBehaviour(new NodeMovementBehaviour()))
			.blockstate(SmallLightConnectorBlock::makeBlockState)
			.item()
			.transform(customItemModel("connector","small_light","item"))
			.register();

	public static final BlockEntry<LargeConnectorBlock> LARGE_CONNECTOR = CreateAddition.REGISTRATE.block("large_connector",  LargeConnectorBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(movementBehaviour(new NodeMovementBehaviour()))
			.blockstate(LargeConnectorBlock::makeBlockState)
			.item()
			.transform(customItemModel("connector","large","item"))
			.register();

	/*public static final BlockEntry<AccumulatorBlock> ACCUMULATOR = CreateAddition.REGISTRATE.block("accumulator",  AccumulatorBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.onRegister(AllMovementBehaviours.movementBehaviour(new NodeMovementBehaviour()))
			.item()
			//.tab()
			.transform(customItemModel())
			.register();*/

	public static final BlockEntry<RedstoneRelayBlock> REDSTONE_RELAY = CreateAddition.REGISTRATE.block("redstone_relay",  RedstoneRelayBlock::new)
			.initialProperties(SharedProperties::stone)
			.onRegister(movementBehaviour(new NodeMovementBehaviour()))
			.blockstate(RedstoneRelayBlock::makeBlockState)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<CACakeBlock> CHOCOLATE_CAKE = CreateAddition.REGISTRATE.block("chocolate_cake",  CACakeBlock::new)
			.initialProperties(() -> Blocks.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.blockstate(CACakeBlock::makeBlockState)
			.item()
			.transform(customItemModel())
			.loot((lt,b) -> lt.add(b, BlockLootSubProvider.noDrop()))
			.register();

	public static final BlockEntry<CACakeBlock> HONEY_CAKE = CreateAddition.REGISTRATE.block("honey_cake",  CACakeBlock::new)
			.initialProperties(() -> Blocks.CAKE)
			.properties(props -> props.sound(SoundType.WOOL).strength(0.5f))
			.blockstate(CACakeBlock::makeBlockState)
			.item()
			.transform(customItemModel())
			.loot((lt,b) -> lt.add(b, BlockLootSubProvider.noDrop()))
			.register();

	/*public static final BlockEntry<HarmfulPlantBlock> HARMFUL_PLANT = CreateAddition.REGISTRATE.block("harmful_plant",  HarmfulPlantBlock::new)
			.initialProperties(Material.PLANT)
			.properties(props -> props.sound(SoundType.CROP).strength(0.5f))
			.item()
			.transform(customItemModel())
			.register();*/

	public static final BlockEntry<BarbedWireBlock> BARBED_WIRE = CreateAddition.REGISTRATE
			.block("barbed_wire",  BarbedWireBlock::new)
			.initialProperties(() -> Blocks.COBWEB)
			.properties(props -> props.noCollission().requiresCorrectToolForDrops().strength(4.0F))
			.blockstate(BarbedWireBlock::makeBlockState)
			.item()
			.transform(customItemModel())
			.register();

	public static final BlockEntry<TeslaCoilBlock> TESLA_COIL = CreateAddition.REGISTRATE
			.block("tesla_coil",  TeslaCoilBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.lightLevel(state -> state.getValue(TeslaCoilBlock.POWERED) ? 10 : 0))
			.blockstate(TeslaCoilBlock::makeBlockState)
			.item(AssemblyOperatorBlockItem::new)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<ModularAccumulatorBlock> MODULAR_ACCUMULATOR = CreateAddition.REGISTRATE
			.block("modular_accumulator",  ModularAccumulatorBlock::regular)
			.initialProperties(SharedProperties::softMetal)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.onRegister(movementBehaviour(new ModularAccumulatorMovement()))
			.onRegister(connectedTextures(ModularAccumulatorCTBehaviour::new))
			.transform(displaySource(CADisplaySources.MODULAR_ACCUMULATOR))
			.addLayer(() -> RenderType::cutoutMipped)
			.blockstate(ModularAccumulatorBlock::makeBlockState)
			.item(ModularAccumulatorBlockItem::new)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<LiquidBlazeBurnerBlock> LIQUID_BLAZE_BURNER = CreateAddition.REGISTRATE.block("liquid_blaze_burner",  LiquidBlazeBurnerBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(DyeColor.GRAY))
			.properties(p -> p.lightLevel(LiquidBlazeBurnerBlock::getLight))
			.transform(pickaxeOnly())
			.addLayer(() -> RenderType::cutoutMipped)
			.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
			.loot((lt,b) -> lt.add(b, LootTable.lootTable()
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(AllBlocks.BLAZE_BURNER))
                                    .when(ExplosionCondition.survivesExplosion())
                    )
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(CAItems.STRAW))
                    )
            ))
			.register();

	public static final BlockEntry<PortableEnergyInterfaceBlock> PORTABLE_ENERGY_INTERFACE = CreateAddition.REGISTRATE.block("portable_energy_interface",  PortableEnergyInterfaceBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.onRegister(movementBehaviour(new PortableEnergyInterfaceMovement()))
			.blockstate((c, p) -> p.directionalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
			.item()
			.transform(customItemModel())
			.addLayer(() -> RenderType::cutoutMipped)
			.register();

	/*public static final BlockEntry<CasingBlock> COPPER_WIRE_CASING = REGISTRATE.block("copper_wire_casing", CasingBlock::new)
			.properties(p -> p.color(MaterialColor.PODZOL))
			.transform(BuilderTransformers.casing(() -> CASpriteShifts.COPPER_WIRE_CASING))
			.register();*/

	public static final BlockEntry<Block> BIOMASS_PALLET = CreateAddition.REGISTRATE
			.block("biomass_pellet_block", Block::new)
			.initialProperties(() -> Blocks.DRIED_KELP_BLOCK)
			.properties(p -> p.mapColor(MapColor.COLOR_GREEN))
			.blockstate(BlockGenHelper.simpleBlock())
			.item(BiomassPelletBlockItem::new)
			.transform(customItemModel())
			.register();

	public static final BlockEntry<Block> ELECTRUM_BLOCK = CreateAddition.REGISTRATE.block("electrum_block", Block::new)
			.initialProperties(() -> Blocks.GOLD_BLOCK)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.blockstate(BlockGenHelper.simpleBlock())
			.item()
			.transform(customItemModel())
			.register();


	public static final BlockEntry<DigitalAdapterBlock> DIGITAL_ADAPTER = CreateAddition.REGISTRATE
			.block("digital_adapter",  DigitalAdapterBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.transform(displaySource(CADisplaySources.DIGITAL_ADAPTER))
//			.item(DigitalAdapterBlockItem::new)
			//.transform(customItemModel())
			.blockstate(BlockGenHelper.simpleBlock())
			.item()
			.transform(customItemModel())
			.register();

	public static void register() {

	}
}
