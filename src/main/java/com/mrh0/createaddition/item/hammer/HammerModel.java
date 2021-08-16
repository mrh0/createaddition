package com.mrh0.createaddition.item.hammer;


import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public class HammerModel extends CustomRenderedItemModel {

	public HammerModel(IBakedModel template) {
		super(template, "overcharged_hammer");
		addPartials("shine");
	}

	@Override
	public ItemStackTileEntityRenderer createRenderer() {
		return new HammerRenderer();
	}
}
