package com.mrh0.createaddition.compat.emi.category;

import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoil;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.emi.CreateEmiAnimations;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Direction;

public class AnimatedTeslaCoil extends CreateEmiAnimations {
    public static void addTeslaCoil(WidgetHolder widgets, int x, int y) {
        widgets.addDrawable(x, y, 0, 0, (matrices, mouseX, mouseY, delta) -> {
            int scale = 22;

            blockElement(CABlocks.TESLA_COIL.getDefaultState().setValue(TeslaCoil.FACING, Direction.DOWN).setValue(TeslaCoil.POWERED, true))
                    .rotateBlock(22.5, 22.5, 0)
                    .scale(scale)
                    .render(matrices);
        });
    }
}