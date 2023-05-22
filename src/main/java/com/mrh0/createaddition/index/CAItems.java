package com.mrh0.createaddition.index;

import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.item.BiomassPellet;
import com.mrh0.createaddition.item.DiamondGritSandpaper;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.HiddenIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;


public class CAItems {

	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
		.creativeModeTab(() -> ModGroup.MAIN);
	
	public static final ItemEntry<Item> CAPACITOR =
			REGISTRATE.item("capacitor", Item::new)
			.register();
	
	public static final ItemEntry<Item> DIAMOND_GRIT =
			REGISTRATE.item("diamond_grit", Item::new)
			.register();
	
	public static final ItemEntry<DiamondGritSandpaper> DIAMOND_GRIT_SANDPAPER = REGISTRATE.item("diamond_grit_sandpaper", DiamondGritSandpaper::new)
			//.transform(CreateRegistrate.customRenderedItem(() -> SandPaperItemRenderer::new))
			//.onRegister(s -> TooltipHelper.referTo(s, AllItems.SAND_PAPER))
			.register();
	
	public static final ItemEntry<Item> ZINC_SHEET =
			REGISTRATE.item("zinc_sheet", Item::new)
			.tag(forgeItemTag("plates/zinc"), PLATES.tag)
			.register();
	
	public static final ItemEntry<Item> BIOMASS =
			REGISTRATE.item("biomass", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();
	
	public static final ItemEntry<BiomassPellet> BIOMASS_PELLET =
			REGISTRATE.item("biomass_pellet", BiomassPellet::new)
			.register();
	
	/*public static final ItemEntry<Multimeter> MULTIMETER =
		REGISTRATE.item("multimeter", Multimeter::new)
			.properties((p) -> p.stacksTo(1))
			.register();*/
	
	/*public static final ItemEntry<OverchargedAlloy> OVERCHARGED_ALLOY =
			REGISTRATE.item("overcharged_alloy", OverchargedAlloy::new)
			.properties(p -> p.rarity(Rarity.UNCOMMON))
			.register();*/
	
	/*public static final ItemEntry<ChargingChromaticCompound> CHARGING_CHROMATIC_COMPOUND =
			REGISTRATE.item("charging_chromatic_compound", ChargingChromaticCompound::new)
			.properties(p -> p.rarity(Rarity.UNCOMMON))
			.properties(p -> p.stacksTo(16))
			.model(AssetLookup.existingItemModel())
			.color(() -> ChromaticCompoundColor::new)
			.register();*/

	/*public static final ItemEntry<OverchargedHammer> OVERCHARGED_HAMMER =
			REGISTRATE.item("overcharged_hammer", OverchargedHammer::new)
			//.transform(CreateRegistrate.customRenderedItem(() -> HammerRenderer::new))
			.model(AssetLookup.itemModelWithPartials())
			//.properties(p -> p.addToolType(ToolType.PICKAXE, 4))
			.properties(p -> p.fireResistant())
			.properties(p -> p.stacksTo(1))
			.properties(p -> p.rarity(Rarity.UNCOMMON))
			.register();*/
	
	public static final ItemEntry<WireSpool> SPOOL =
			REGISTRATE.item("spool", WireSpool::new).register();
	
	public static final ItemEntry<WireSpool> COPPER_SPOOL =
			REGISTRATE.item("copper_spool", WireSpool::new)
			.register();
	
	public static final ItemEntry<Item> COPPER_WIRE =
			REGISTRATE.item("copper_wire", Item::new).register();
	public static final ItemEntry<Item> COPPER_ROD =
			REGISTRATE.item("copper_rod", Item::new).register();

	//public static final ItemEntry<Item> IRON_SPOOL =
	//		REGISTRATE.item("iron_spool", Item::new).register();
	public static final ItemEntry<Item> IRON_WIRE =
			REGISTRATE.item("iron_wire", Item::new).register();
	public static final ItemEntry<Item> IRON_ROD =
			REGISTRATE.item("iron_rod", Item::new).register();

	public static final ItemEntry<WireSpool> GOLD_SPOOL =
			REGISTRATE.item("gold_spool", WireSpool::new).register();
	public static final ItemEntry<Item> GOLD_WIRE =
			REGISTRATE.item("gold_wire", Item::new).register();
	public static final ItemEntry<Item> GOLD_ROD =
			REGISTRATE.item("gold_rod", Item::new).register();
	
	public static final ItemEntry<Item> BRASS_ROD =
			REGISTRATE.item("brass_rod", Item::new).register();
	
	public static final ItemEntry<WireSpool> FESTIVE_SPOOL =
			REGISTRATE.item("festive_spool", WireSpool::new).register();
	
	public static final ItemEntry<HiddenIngredientItem> CAKE_BASE =
			REGISTRATE.item("cake_base", HiddenIngredientItem::new)
				.register();
	public static final ItemEntry<HiddenIngredientItem> CAKE_BASE_BAKED =
			REGISTRATE.item("cake_base_baked", HiddenIngredientItem::new)
				.register();
	
	public static final ItemEntry<Item> STRAW =
			REGISTRATE.item("straw", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();
	
	public static void register() {
		REGISTRATE.addToSection(DIAMOND_GRIT_SANDPAPER, AllSections.MATERIALS);
		//Create.registrate().addToSection(MULTIMETER, AllSections.KINETICS);
		REGISTRATE.addToSection(COPPER_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(GOLD_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(FESTIVE_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(SPOOL, AllSections.MATERIALS);
		
		REGISTRATE.addToSection(BIOMASS_PELLET, AllSections.MATERIALS);
		//Create.registrate().addToSection(OVERCHARGED_ALLOY, AllSections.MATERIALS);
		//Create.registrate().addToSection(OVERCHARGED_HAMMER, AllSections.CURIOSITIES);
		
		REGISTRATE.addToSection(STRAW, AllSections.MATERIALS);
	}
}