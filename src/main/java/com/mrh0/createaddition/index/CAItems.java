package com.mrh0.createaddition.index;

import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.item.DiamondGritSandpaper;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.curiosities.tools.SandPaperItemRenderer.SandPaperModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

public class CAItems {

	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
		.itemGroup(() -> ModGroup.MAIN);
	
	public static final ItemEntry<Item> CAPACITOR =
			REGISTRATE.item("capacitor", Item::new)
			.register();
	
	public static final ItemEntry<Item> DIAMOND_GRIT =
			REGISTRATE.item("diamond_grit", Item::new)
			.register();
	
	public static final ItemEntry<DiamondGritSandpaper> DIAMOND_GRIT_SANDPAPER = REGISTRATE.item("diamond_grit_sandpaper", DiamondGritSandpaper::new)
			.transform(CreateRegistrate.customRenderedItem(() -> SandPaperModel::new))
			.onRegister(s -> TooltipHelper.referTo(s, AllItems.SAND_PAPER))
			.register();
	
	public static final ItemEntry<Item> ZINC_SHEET =
			REGISTRATE.item("zinc_sheet", Item::new)
			.tag(forgeItemTag("plates/zinc"), PLATES.tag)
			.register();
	
	public static final ItemEntry<Multimeter> MULTIMETER =
		REGISTRATE.item("multimeter", Multimeter::new)
			.properties((p) -> p.maxStackSize(1))
			.register();
	
	public static final ItemEntry<WireSpool> SPOOL =
			REGISTRATE.item("spool", WireSpool::new).register();
	
	public static final ItemEntry<WireSpool> COPPER_SPOOL =
			REGISTRATE.item("copper_spool", WireSpool::new).register();
	
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
	
	/*public static final ItemEntry<WireSpool> DEVTOOL =
			REGISTRATE.item("devtool", WireSpool::new)
				.properties((p) -> p.maxStackSize(1))
				.register();*/
	
	
	
	public static void register() {
		Create.registrate().addToSection(DIAMOND_GRIT_SANDPAPER, AllSections.MATERIALS);
		Create.registrate().addToSection(MULTIMETER, AllSections.KINETICS);
		Create.registrate().addToSection(COPPER_SPOOL, AllSections.MATERIALS);
		Create.registrate().addToSection(GOLD_SPOOL, AllSections.MATERIALS);
		Create.registrate().addToSection(SPOOL, AllSections.MATERIALS);
	}
}
