package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedRollingMill extends AnimatedKinetics {

	boolean shadow = true;

	public AnimatedRollingMill(boolean shadow) { this.shadow = shadow; }

	public AnimatedRollingMill() {}

	@Override
	public void draw(GuiGraphics matrixStack, int xOffset, int yOffset) {
		matrixStack.pose().pushPose();
		matrixStack.pose().translate(xOffset, yOffset, 0);
		if(shadow)
			AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);
		matrixStack.pose().translate(-2, 18, 0);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-22.5f));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
		int scale = 22;

		blockElement(shaft(Direction.Axis.Z))
				.rotateBlock(0, 0, -getCurrentAngle())
				.scale(scale)
				.render(matrixStack);

		blockElement(shaft(Direction.Axis.Z))
				.atLocal(0f, -4f/16F, 0f)
				.rotateBlock(0, 0, getCurrentAngle())
				.scale(scale)
				.render(matrixStack);

		blockElement(CABlocks.ROLLING_MILL.getDefaultState())
			.rotateBlock(0f, 0f, 0)
			.scale(scale)
			.render(matrixStack);

		matrixStack.pose().popPose();
	}

}