package com.mrh0.createaddition.compat.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.compat.jei.category.AnimatedRollingMill;
import com.simibubi.create.compat.rei.category.sequencedAssembly.ReiSequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;

public class ReiRollingMillAssemblySubCategory extends ReiSequencedAssemblySubCategory {

    AnimatedRollingMill mill;

    public ReiRollingMillAssemblySubCategory() {
        super(20);
        mill = new AnimatedRollingMill(false);
    }

    @Override
    public void draw(SequencedRecipe<?> sequencedRecipe, PoseStack ms, double mouseX, double mouseY, int index) {
        ms.pushPose();
        ms.translate(0, 51.5f, 0);
        ms.scale(.6f, .6f, .6f);
        mill.draw(ms, getWidth() / 2, 30);
        ms.popPose();
    }
}
