package com.mrh0.createaddition.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;

public class BiomassPellet extends Item {

	public BiomassPellet(Properties props) {
		super(props);
		FuelRegistry.INSTANCE.add(this, getBurnTime());
	}

	public int getBurnTime() {
		return 6400;
	}
}
