package com.mrh0.createaddition.entities.overcharged_hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class OverchargedHammerModel extends EntityModel<Entity> {
	private final ModelRenderer bone;

	public OverchargedHammerModel() {
		textureWidth = 64;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.setTextureOffset(40, 8).addCuboid(-1.0F, 8.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F, 0.0F, false);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}