package com.mrh0.createaddition.compat.emi.category;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.emi.CreateEmiAnimations;
import dev.emi.emi.api.widget.WidgetHolder;

public class AnimatedRollingMill extends CreateEmiAnimations {
    public static void addRollingMill(WidgetHolder widgets, int x, int y) {
        widgets.addDrawable(x, y, 0, 0, (matrices, mouseX, mouseY, delta) -> {
            int scale = 22;

            blockElement(CABlocks.ROLLING_MILL.getDefaultState())
                    .rotateBlock(22.5, 22.5, 0)
                    .scale(scale)
                    .render(matrices);
        });
    }
}
