package com.mrh0.createaddition.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.GuiGameElement;

public class AnimatedRollingMill extends AnimatedKinetics {

	public void draw(int xOffset, int yOffset) {
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)xOffset, (float)yOffset, 0.0F);
		AllGuiTextures.JEI_SHADOW.draw(-16, 13);
		RenderSystem.translatef(-2.0F, 18.0F, 0.0F);

		int scale = 22;
		/*GuiGameElement.of(AllBlockPartials.MILLSTONE_COG)
			.rotateBlock(22.5, getCurrentAngle() * 2, 0)
			.scale(scale)
			.render(matrixStack);*/
		
		GuiGameElement.of(CABlocks.ROLLING_MILL.getDefaultState())
			.rotateBlock(22.5, 22.5, 0)
			.scale(scale)
			.render();

		RenderSystem.popMatrix();
	}

}