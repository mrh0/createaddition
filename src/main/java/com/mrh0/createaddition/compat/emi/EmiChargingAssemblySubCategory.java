package com.mrh0.createaddition.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Direction;

public class EmiChargingAssemblySubCategory extends EmiSequencedAssemblySubCategory {

    public EmiChargingAssemblySubCategory() {
        super(25);
    }

    @Override
    public void addWidgets(WidgetHolder widgets, int x, int y, SequencedRecipe<?> recipe, int index) {
        widgets.addDrawable(x, y, getWidth(), 96, (graphics, mouseX, mouseY, delta) -> {
            PoseStack matrices = graphics.pose();
            float scale = 0.6f;
            matrices.translate(0, 51.5, 0);
            matrices.scale(scale, scale, scale);
            CAEmiAnimations.renderTeslaCoil(graphics, index, Direction.UP, true);
        }).tooltip(getTooltip(recipe, index));
    }
}
