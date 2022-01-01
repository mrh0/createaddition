package com.mrh0.createaddition.entities.overcharged_hammer;
/*

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class OverchargedHammerRenderer extends EntityRenderer<OverchargedHammerEntity> {
	public static final ResourceLocation TEX = new ResourceLocation("createaddition:textures/entity/overcharged_hammer.png");
	private final OverchargedHammerModel model;
	//private ModelLayerLocation loc = new ModelLayerLocation(new ResourceLocation(CreateAddition.MODID, "overcharged_hammer"), "main");

	public OverchargedHammerRenderer(Context p_174420_) {
	      super(p_174420_);
	      this.model = new OverchargedHammerModel();//
	   }

	   public void render(OverchargedHammerEntity p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
	      p_116114_.pushPose();
	      p_116114_.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot()) - 90.0F));
	      p_116114_.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot()) + 90.0F));
	      VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(p_116115_, this.model.renderType(this.getTextureLocation(p_116111_)), false, p_116111_.isEnchanted());
	      this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	      p_116114_.popPose();
	      super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
	   }

	@Override
	public ResourceLocation getTextureLocation(OverchargedHammerEntity p_114482_) {
		return TEX;
	}
}
*/