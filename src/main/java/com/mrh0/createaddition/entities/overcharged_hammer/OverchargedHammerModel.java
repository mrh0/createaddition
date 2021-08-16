package com.mrh0.createaddition.entities.overcharged_hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class OverchargedHammerModel extends EntityModel<Entity> {
	private final ModelRenderer bone;
	private final ResourceLocation tex = new ResourceLocation("createaddition", "textures/entity/overcharged_hammer.png");

	public OverchargedHammerModel() {
		texWidth = 64;
		texHeight = 64;

		bone = new ModelRenderer(this);
		bone.setPos(0.0F, 0.0F, 0.0F);
		bone.texOffs(40, 8).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
		bone.texOffs(0, 0).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F, 0.0F, false);
		bone.texOffs(0, 20).addBox(-3.5F, 0.5F, -5.5F, 7.0F, 7.0F, 11.0F, 1.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		IVertexBuilder ivertexbuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityTranslucent(tex));
		bone.render(matrixStack, ivertexbuilder, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}