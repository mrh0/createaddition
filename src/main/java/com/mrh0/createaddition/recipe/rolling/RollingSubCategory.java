package com.mrh0.createaddition.recipe.rolling;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.compat.jei.AnimatedRollingMill;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedRecipe;

public class RollingSubCategory extends SequencedAssemblySubCategory {

    AnimatedRollingMill mill;

    public RollingSubCategory() {
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
