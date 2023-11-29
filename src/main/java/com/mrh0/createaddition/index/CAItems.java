package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.item.BiomassPellet;
import com.mrh0.createaddition.item.DiamondGritSandpaper;
import com.mrh0.createaddition.item.WireSpool;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import static com.simibubi.create.AllTags.forgeItemTag;


@SuppressWarnings({"unused", "CommentedOutCode"})
public class CAItems {

	static {
		CreateAddition.REGISTRATE.useCreativeTab(ModGroup.MAIN_KEY);
	}
	
	public static final ItemEntry<Item> CAPACITOR =
			CreateAddition.REGISTRATE.item("capacitor", Item::new)
			.register();
	
	public static final ItemEntry<Item> DIAMOND_GRIT =
			CreateAddition.REGISTRATE.item("diamond_grit", Item::new)
			.register();
	
	public static final ItemEntry<DiamondGritSandpaper> DIAMOND_GRIT_SANDPAPER = CreateAddition.REGISTRATE.item("diamond_grit_sandpaper", DiamondGritSandpaper::new)
			.transform(CreateRegistrate.customRenderedItem(() -> SandPaperItemRenderer::new))
			//.onRegister(s -> TooltipHelper.referTo(s, AllItems.SAND_PAPER))
			.register();
	
	public static final ItemEntry<Item> ZINC_SHEET =
			CreateAddition.REGISTRATE.item("zinc_sheet", Item::new)
			.tag(forgeItemTag("plates/zinc"), PLATES.tag)
			.register();
	
	public static final ItemEntry<Item> BIOMASS =
			CreateAddition.REGISTRATE.item("biomass", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();
	
	public static final ItemEntry<BiomassPellet> BIOMASS_PELLET =
			CreateAddition.REGISTRATE.item("biomass_pellet", BiomassPellet::new)
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
			CreateAddition.REGISTRATE.item("spool", WireSpool::new).register();
	
	public static final ItemEntry<WireSpool> COPPER_SPOOL =
			CreateAddition.REGISTRATE.item("copper_spool", WireSpool::new)
			.register();
	
	public static final ItemEntry<Item> COPPER_WIRE =
			CreateAddition.REGISTRATE.item("copper_wire", Item::new).register();
	public static final ItemEntry<Item> COPPER_ROD =
			CreateAddition.REGISTRATE.item("copper_rod", Item::new).register();

	//public static final ItemEntry<Item> IRON_SPOOL =
	//		REGISTRATE.item("iron_spool", Item::new).register();
	public static final ItemEntry<Item> IRON_WIRE =
			CreateAddition.REGISTRATE.item("iron_wire", Item::new).register();
	public static final ItemEntry<Item> IRON_ROD =
			CreateAddition.REGISTRATE.item("iron_rod", Item::new).register();

	public static final ItemEntry<WireSpool> GOLD_SPOOL =
			CreateAddition.REGISTRATE.item("gold_spool", WireSpool::new).register();
	public static final ItemEntry<Item> GOLD_WIRE =
			CreateAddition.REGISTRATE.item("gold_wire", Item::new).register();
	public static final ItemEntry<Item> GOLD_ROD =
			CreateAddition.REGISTRATE.item("gold_rod", Item::new).register();
	
	public static final ItemEntry<Item> BRASS_ROD =
			CreateAddition.REGISTRATE.item("brass_rod", Item::new).register();

	public static final ItemEntry<WireSpool> FESTIVE_SPOOL =
			CreateAddition.REGISTRATE.item("festive_spool", WireSpool::new).register();
	
	public static final ItemEntry<Item> CAKE_BASE =
			CreateAddition.REGISTRATE.item("cake_base", Item::new)
				.register();
	public static final ItemEntry<Item> CAKE_BASE_BAKED =
			CreateAddition.REGISTRATE.item("cake_base_baked", Item::new)
				.register();

	public static final ItemEntry<Item> STRAW =
			CreateAddition.REGISTRATE.item("straw", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();

	public static void register() {
		/*REGISTRATE.addToSection(DIAMOND_GRIT_SANDPAPER, AllSections.MATERIALS);
		//Create.registrate().addToSection(MULTIMETER, AllSections.KINETICS);
		REGISTRATE.addToSection(COPPER_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(GOLD_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(FESTIVE_SPOOL, AllSections.MATERIALS);
		REGISTRATE.addToSection(SPOOL, AllSections.MATERIALS);

		REGISTRATE.addToSection(BIOMASS_PELLET, AllSections.MATERIALS);
		//Create.registrate().addToSection(OVERCHARGED_ALLOY, AllSections.MATERIALS);
		//Create.registrate().addToSection(OVERCHARGED_HAMMER, AllSections.CURIOSITIES);
		
		REGISTRATE.addToSection(STRAW, AllSections.MATERIALS);*/
	}
}