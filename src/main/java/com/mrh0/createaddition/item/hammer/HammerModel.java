package com.mrh0.createaddition.item.hammer;


import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public class HammerModel extends CustomRenderedItemModel {

	public HammerModel(IBakedModel template) {
		super(template, CreateAddition.MODID, "overcharged_hammer");
		addPartials("shine");
	}
}
