package com.mrh0.createaddition.entities.overcharged_hammer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class OverchargedHammerRenderer extends EntityRenderer<OverchargedHammerEntity> {
	public static final ResourceLocation TEX = new ResourceLocation("createaddition:textures/entity/overcharged_hammer.png");
	private final OverchargedHammerModel model = new OverchargedHammerModel();

	public OverchargedHammerRenderer(Context erm) {
		super(erm);
	}

	@Override
	public void render(OverchargedHammerEntity entity, float x, float y, PoseStack stack,
			MultiBufferSource buff, int i) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(
				Mth.lerp(y, entity.yRotO, entity.yRotO) - 90.0F));
		stack.mulPose(Vector3f.ZP.rotationDegrees(
				Mth.lerp(y, entity.xRotO, entity.xRotO) + 90.0F));
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(
				buff, this.model.renderType(this.getTextureLocation(entity)), false, entity.isEnchanted());
		this.model.renderToBuffer(stack, ivertexbuilder, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.popPose();
		super.render(entity, x, y, stack, buff, i);
	}

	public ResourceLocation getTextureLocation(OverchargedHammerEntity p_110775_1_) {
		return TEX;
	}
	
	@Override
	public boolean shouldRender(OverchargedHammerEntity ent, Frustum clipp, double x, double y, double z) {
		return true;
	}
}
