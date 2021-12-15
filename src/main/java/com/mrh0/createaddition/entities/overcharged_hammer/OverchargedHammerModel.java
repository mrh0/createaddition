package com.mrh0.createaddition.entities.overcharged_hammer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class OverchargedHammerModel extends Model {
	private final ModelPart root;
	private final ResourceLocation tex = new ResourceLocation("createaddition",
			"textures/entity/overcharged_hammer.png");

	public OverchargedHammerModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition pd1 = partdefinition.addOrReplaceChild("p1",
				CubeListBuilder.create().texOffs(40, 8).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 10.0F, 2.0F), PartPose.ZERO);
		
		pd1.addOrReplaceChild("p2",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F), PartPose.ZERO);
		
		pd1.addOrReplaceChild("p3",
				CubeListBuilder.create().texOffs(0, 20).addBox(-3.5F, 0.5F, -5.5F, 7.0F, 7.0F, 11.0F), PartPose.ZERO);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer vert, int a, int b,
			float x, float y, float z, float c) {
		this.root.render(stack, vert, a, b, x, y,z, c);
	}
}

/*
 * public OverchargedHammerModel() { texWidth = 64; texHeight = 64;
 * 
 * bone = new ModelPart(this); bone.setPos(0.0F, 0.0F, 0.0F); bone.texOffs(40,
 * 8).addBox(-1.0F, 8.0F, -1.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
 * bone.texOffs(0, 0).addBox(-4.0F, 0.0F, -6.0F, 8.0F, 8.0F, 12.0F, 0.0F,
 * false); bone.texOffs(0, 20).addBox(-3.5F, 0.5F, -5.5F, 7.0F, 7.0F, 11.0F,
 * 1.0F, false); }
 * 
 * @Override public void setupAnim(Entity entity, float limbSwing, float
 * limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
 * //previously the render function, render code was moved to a method below }
 * 
 * @Override public void renderToBuffer(PoseStack matrixStack, VertexConsumer
 * buffer, int packedLight, int packedOverlay, float red, float green, float
 * blue, float alpha) { VertexConsumer ivertexbuilder =
 * Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.
 * entityTranslucent(tex)); bone.buffer(ivertexbuilder);//.render(matrixStack,
 * ivertexbuilder, packedLight, packedOverlay); }
 * 
 * public void setRotationAngle(ModelPart modelRenderer, float x, float y, float
 * z) { modelRenderer.xRot = x; modelRenderer.yRot = y; modelRenderer.zRot = z;
 * }
 */
