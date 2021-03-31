package com.mrh0.createaddition.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

public class WireItemRenderer extends ItemRenderer {

	public WireItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
		super(textureManagerIn, modelManagerIn, itemColorsIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void renderItem(ItemStack itemStackIn, TransformType transformTypeIn, boolean leftHand,
			MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn,
			IBakedModel modelIn) {
		super.renderItem(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn,
				modelIn);
	}
}
