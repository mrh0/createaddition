package com.mrh0.createaddition.index;

import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.item.ElectrumAmulet;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.item.BiomassPellet;
import com.mrh0.createaddition.item.DiamondGritSandpaper;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.world.item.Item;


public class CAItems {

	static {
		CreateAddition.REGISTRATE.setCreativeTab(CACreativeModeTabs.MAIN_TAB);
	}

	public static final ItemEntry<Item> CAPACITOR =
			CreateAddition.REGISTRATE.item("capacitor", Item::new)
			.register();

	public static final ItemEntry<Item> DIAMOND_GRIT =
			CreateAddition.REGISTRATE.item("diamond_grit", Item::new)
			.register();
	public static final ItemEntry<DiamondGritSandpaper> DIAMOND_GRIT_SANDPAPER = CreateAddition.REGISTRATE.item("diamond_grit_sandpaper", DiamondGritSandpaper::new)
			.register();

	public static final ItemEntry<Item> BIOMASS =
			CreateAddition.REGISTRATE.item("biomass", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();
	public static final ItemEntry<BiomassPellet> BIOMASS_PELLET =
			CreateAddition.REGISTRATE.item("biomass_pellet", BiomassPellet::new)
			.register();

	public static final ItemEntry<ElectrumAmulet> ELECTRUM_AMULET =
			CreateAddition.REGISTRATE.item("electrum_amulet", ElectrumAmulet::new)
					.register();

	public static final ItemEntry<Item> ELECTRUM_INGOT =
			CreateAddition.REGISTRATE.item("electrum_ingot", Item::new).register();
	public static final ItemEntry<Item> ELECTRUM_NUGGET =
			CreateAddition.REGISTRATE.item("electrum_nugget", Item::new).register();

	public static final ItemEntry<Item> ELECTRUM_SHEET =
			CreateAddition.REGISTRATE.item("electrum_sheet", Item::new)
					.tag(forgeItemTag("plates/electrum"), PLATES.tag)
					.register();
	public static final ItemEntry<Item> ZINC_SHEET =
			CreateAddition.REGISTRATE.item("zinc_sheet", Item::new)
					.tag(forgeItemTag("plates/zinc"), PLATES.tag)
					.register();

	public static final ItemEntry<Item> COPPER_WIRE =
			CreateAddition.REGISTRATE.item("copper_wire", Item::new).register();
	public static final ItemEntry<Item> IRON_WIRE =
			CreateAddition.REGISTRATE.item("iron_wire", Item::new).register();
	public static final ItemEntry<Item> GOLD_WIRE =
			CreateAddition.REGISTRATE.item("gold_wire", Item::new).register();
	public static final ItemEntry<Item> ELECTRUM_WIRE =
			CreateAddition.REGISTRATE.item("electrum_wire", Item::new).register();

	public static final ItemEntry<WireSpool> SPOOL =
			CreateAddition.REGISTRATE.item("spool", WireSpool::new).register();
	public static final ItemEntry<WireSpool> COPPER_SPOOL =
			CreateAddition.REGISTRATE.item("copper_spool", WireSpool::new).register();
	//public static final ItemEntry<WireSpool> IRON_SPOOL =
	//		CreateAddition.REGISTRATE.item("iron_spool", WireSpool::new).register();
	public static final ItemEntry<WireSpool> GOLD_SPOOL =
			CreateAddition.REGISTRATE.item("gold_spool", WireSpool::new).register();
	public static final ItemEntry<WireSpool> ELECTRUM_SPOOL =
			CreateAddition.REGISTRATE.item("electrum_spool", WireSpool::new).register();
	public static final ItemEntry<WireSpool> FESTIVE_SPOOL =
			CreateAddition.REGISTRATE.item("festive_spool", WireSpool::new).register();

	public static final ItemEntry<Item> COPPER_ROD =
			CreateAddition.REGISTRATE.item("copper_rod", Item::new).register();
	public static final ItemEntry<Item> IRON_ROD =
			CreateAddition.REGISTRATE.item("iron_rod", Item::new).register();
	public static final ItemEntry<Item> GOLD_ROD =
			CreateAddition.REGISTRATE.item("gold_rod", Item::new).register();
	public static final ItemEntry<Item> ELECTRUM_ROD =
			CreateAddition.REGISTRATE.item("electrum_rod", Item::new).register();
	public static final ItemEntry<Item> BRASS_ROD =
			CreateAddition.REGISTRATE.item("brass_rod", Item::new).register();

	public static final ItemEntry<Item> CAKE_BASE =
			CreateAddition.REGISTRATE.item("cake_base", Item::new) // TODO: Hide in Tab
				.register();
	public static final ItemEntry<Item> CAKE_BASE_BAKED =
			CreateAddition.REGISTRATE.item("cake_base_baked", Item::new) // TODO: Hide in Tab
				.register();

	public static final ItemEntry<Item> STRAW =
			CreateAddition.REGISTRATE.item("straw", Item::new)
			.properties(p -> p.stacksTo(16))
			.register();

	public static void register() {

	}
}
