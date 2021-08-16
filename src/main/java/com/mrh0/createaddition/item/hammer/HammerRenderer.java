package com.mrh0.createaddition.item.hammer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class HammerRenderer  extends CustomRenderedItemModelRenderer<HammerModel> {

	@Override
	protected void render(ItemStack stack, HammerModel model, PartialItemModelRenderer renderer, ItemCameraTransforms.TransformType transformType,
		MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTime() / 20;
		int maxLight = 0xF000F0;

		renderer.render(model.getOriginalModel(), light);
		//renderer.renderSolidGlowing(model.getPartial("handle"), maxLight);
		renderer.renderGlowing(model.getPartial("shine"), maxLight);

		/*float floating = MathHelper.sin(worldTime) * .05f;
		float angle = worldTime * -10 % 360;

		ms.translate(0, floating, 0);
		ms.mulPose(Vector3f.YP.rotationDegrees(angle));

		renderer.renderGlowing(model.getPartial("shine"), maxLight);*/
	}

}
