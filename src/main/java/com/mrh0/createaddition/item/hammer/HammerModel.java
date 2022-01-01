package com.mrh0.createaddition.item.hammer;


import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.minecraft.client.resources.model.BakedModel;

public class HammerModel extends CustomRenderedItemModel {

	public HammerModel(BakedModel template) {
		super(template, CreateAddition.MODID, "overcharged_hammer");
		addPartials("shine");
	}
}
