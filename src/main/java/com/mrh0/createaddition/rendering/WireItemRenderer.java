package com.mrh0.createaddition.rendering;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.ItemStack;

public class WireItemRenderer extends ItemRenderer {

	public WireItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
		super(textureManagerIn, modelManagerIn, itemColorsIn, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(ItemStack itemStackIn, TransformType transformTypeIn, boolean leftHand,
			PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn,
			BakedModel modelIn) {
		super.render(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
				modelIn);
	}
}
