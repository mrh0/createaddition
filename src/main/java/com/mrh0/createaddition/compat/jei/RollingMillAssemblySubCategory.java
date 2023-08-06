package com.mrh0.createaddition.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;

public class RollingMillAssemblySubCategory extends SequencedAssemblySubCategory {

    AnimatedRollingMill mill;

    public RollingMillAssemblySubCategory() {
        super(20);
        mill = new AnimatedRollingMill(false);
    }

    @Override
    public void draw(SequencedRecipe<?> sequencedRecipe, GuiGraphics gg, double mouseX, double mouseY, int index) {
        var ms = gg.pose();
        ms.pushPose();
        ms.translate(0, 51.5f, 0);
        ms.scale(.6f, .6f, .6f);
        mill.draw(gg, getWidth() / 2, 30);
        ms.popPose();
    }
}
