package com.mrh0.createaddition.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import dev.emi.emi.api.widget.WidgetHolder;

public class EmiRollingMillAssemblySubCategory extends EmiSequencedAssemblySubCategory {

    public EmiRollingMillAssemblySubCategory() {
        super(25);
    }

    @Override
    public void addWidgets(WidgetHolder widgets, int x, int y, SequencedRecipe<?> recipe, int index) {

        widgets.addDrawable(x, y, getWidth(), 96, (graphics, mouseX, mouseY, delta) -> {
            PoseStack matrices = graphics.pose();
            matrices.translate(0, 54.5f, 0);
            float scale = 0.6f;
            matrices.scale(scale, scale, scale);
            matrices.translate(getWidth() / 2, 30, 0);
            CAEmiAnimations.renderRoller(graphics, index);
        }).tooltip(getTooltip(recipe, index));
    }
}
