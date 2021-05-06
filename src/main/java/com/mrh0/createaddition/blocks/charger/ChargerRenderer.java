package com.mrh0.createaddition.blocks.charger;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class ChargerRenderer  extends TileEntityRenderer<ChargerTileEntity> {

	public ChargerRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	private static final float u1 = 1f/16f;
	private static final float scalar = 14f/16f;

	@Override
	public void render(ChargerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction dir = tileEntityIn.getBlockState().get(Charger.FACING);
		
		ItemStack item = tileEntityIn.getChargedStack();
		
		if(!tileEntityIn.hasChargedStack())
			return;
		
		matrixStackIn.push();
		matrixStackIn.translate(.5f, u1*13.5f, .5f);
		matrixStackIn.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(dir.getHorizontalAngle() - 90f));
		matrixStackIn.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f));
		matrixStackIn.scale(scalar, scalar, scalar);
        Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
        matrixStackIn.pop();
	}

}
