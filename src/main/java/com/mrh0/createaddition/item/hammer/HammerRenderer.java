package com.mrh0.createaddition.item.hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;


public class HammerRenderer  extends CustomRenderedItemModelRenderer<HammerModel> {
	
	@Override
	protected void render(ItemStack stack, HammerModel model, PartialItemModelRenderer renderer, ItemTransforms.TransformType transformType,
		PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTime() / 10;
		int maxLight = 0xF000F0;

		renderer.render(model.getOriginalModel(), light);
		
		float f = (float) ((1+Math.sin(worldTime)) * .04f);
		
		ms.translate(0f, -f/2f, 0f);
		ms.scale(1f+f, 1f+f, 1f+f);
		renderer.renderGlowing(model.getPartial("shine"), maxLight);
	}

	@Override
	public HammerModel createModel(BakedModel originalModel) {
		return new HammerModel(originalModel);
	}

}
