package com.mrh0.createaddition.entities.overcharged_hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class OverchargedHammerRenderer extends EntityRenderer<OverchargedHammerEntity> {
	public static final ResourceLocation TEX = new ResourceLocation("createaddition:textures/entity/overcharged_hammer.png");
	private final OverchargedHammerModel model = new OverchargedHammerModel();

	public OverchargedHammerRenderer(EntityRendererManager erm) {
		super(erm);
	}

	public void render(OverchargedHammerEntity entity, float x, float y,
			MatrixStack stack, IRenderTypeBuffer buff, int i) {
		stack.push();
		stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(
				MathHelper.lerp(y, entity.prevRotationYaw, entity.rotationYaw) - 90.0F));
		stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(
				MathHelper.lerp(y, entity.prevRotationPitch, entity.rotationPitch) + 90.0F));
		IVertexBuilder ivertexbuilder = net.minecraft.client.renderer.ItemRenderer.getDirectGlintVertexConsumer(
				buff, this.model.getLayer(this.getEntityTexture(entity)), false, entity.isEnchanted());
		this.model.render(stack, ivertexbuilder, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		stack.pop();
		super.render(entity, x, y, stack, buff, i);
	}

	public ResourceLocation getEntityTexture(OverchargedHammerEntity p_110775_1_) {
		return TEX;
	}
	
	@Override
	public boolean shouldRender(OverchargedHammerEntity ent, ClippingHelper clipp, double x, double y, double z) {
		return true;
	}
}
