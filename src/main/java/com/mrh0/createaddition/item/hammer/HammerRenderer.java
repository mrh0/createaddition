package com.mrh0.createaddition.item.hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class HammerRenderer  extends CustomRenderedItemModelRenderer<HammerModel> {

	@Override
	protected void render(ItemStack stack, HammerModel model, PartialItemModelRenderer renderer, ItemCameraTransforms.TransformType transformType,
		MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTime() / 10;
		int maxLight = 0xF000F0;

		renderer.render(model.getOriginalModel(), light);
		
		float f = (1+MathHelper.sin(worldTime)) * .04f;
		
		ms.translate(0f, -f/2f, 0f);
		ms.scale(1f+f, 1f+f, 1f+f);
		renderer.renderGlowing(model.getPartial("shine"), maxLight);
	}

	@Override
	public HammerModel createModel(IBakedModel originalModel) {
		return new HammerModel(originalModel);
	}

}
