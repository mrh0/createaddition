package com.mrh0.createaddition.entities.overcharged_hammer;


import com.jozufozu.flywheel.core.model.ModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class OverchargedHammerModel extends EntityModel<Entity> {
	private final ModelPart bone;
	private final ResourceLocation tex = new ResourceLocation("createaddition", "textures/entity/overcharged_hammer.png");

	public OverchargedHammerModel() {
		texWidth = 64;
		texHeight = 64;

		bone = new ModelPart(this);
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
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		VertexConsumer ivertexbuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityTranslucent(tex));
		bone.buffer(ivertexbuilder);//.render(matrixStack, ivertexbuilder, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}