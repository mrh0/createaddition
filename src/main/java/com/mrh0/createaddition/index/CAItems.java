package com.mrh0.createaddition.index;

import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.mrh0.createaddition.item.DiamondGritSandpaper;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.content.curiosities.tools.SandPaperItemRenderer.SandPaperModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;

import net.minecraft.item.Item;

public class CAItems {

	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
		.itemGroup(() -> ModGroup.MAIN);
	
	public static final ItemEntry<Item> CAPACITOR =
			REGISTRATE.item("capacitor", Item::new)
			.register();
	
	public static final ItemEntry<Item> COPPER_SPOOL =
			REGISTRATE.item("copper_spool", Item::new)
			.register();
	
	public static final ItemEntry<Item> COPPER_WIRE =
			REGISTRATE.item("copper_wire", Item::new)
			.register();
	
	public static final ItemEntry<Item> DIAMOND_GRIT =
			REGISTRATE.item("diamond_grit", Item::new)
			.register();
	
	public static final ItemEntry<DiamondGritSandpaper> DIAMOND_GRIT_SANDPAPER = REGISTRATE.item("diamond_grit_sandpaper", DiamondGritSandpaper::new)
			.transform(CreateRegistrate.customRenderedItem(() -> SandPaperModel::new))
			.register();
	
	public static final ItemEntry<Item> ZINC_SHEET =
			REGISTRATE.item("zinc_sheet", Item::new)
			.tag(forgeItemTag("plates/zinc"), PLATES.tag)
			.register();
	
	public static final ItemEntry<Multimeter> MULTIMETER =
		REGISTRATE.item("multimeter", Multimeter::new)
			.register();
	
	public static void register() {}
}
