package com.mrh0.createaddition.compat.jei;

import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedTeslaCoil extends AnimatedKinetics {

	private Direction dir = Direction.DOWN;

	public AnimatedTeslaCoil(Direction dir) {
		this.dir = dir;
	}

	public AnimatedTeslaCoil() {
		dir = Direction.DOWN;
	}

	@Override
	public void draw(GuiGraphics matrixStack, int xOffset, int yOffset) {
		matrixStack.pose().pushPose();
		matrixStack.pose().translate(xOffset, yOffset, 0);
		AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);
		matrixStack.pose().translate(-2, 18, 0);
		int scale = 22;
		
		GuiGameElement.of(CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoilBlock.FACING, dir).setValue(TeslaCoilBlock.POWERED, true))
			.rotateBlock(22.5, 22.5, 0)
			.scale(scale)
			.render(matrixStack);

		matrixStack.pose().popPose();
	}

}