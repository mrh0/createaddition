package com.mrh0.createaddition.entities.overcharged_hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class OverchargedHammerModel extends EntityModel<Entity> {
	private final ModelRenderer bone;

	public OverchargedHammerModel() {
		texWidth = 64;
		texHeight = 32;

		bone = new ModelRenderer(this);
		bone.setPos(0.0F, 0.0F, 0.0F);
		bone.texOffs(40, 8).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
		bone.texOffs(0, 0).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}