package com.mrh0.createaddition.item;

import com.mrh0.createaddition.config.Config;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;

public class DiamondGritSandpaper extends SandPaperItem {

	private static final int USES = Config.DIAMOND_GRIT_SANDPAPER_USES.get();
	
	public DiamondGritSandpaper(Properties properties) {
		super(properties.defaultDurability(USES));
	}
}
