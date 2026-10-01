package com.mrh0.createaddition.compat.jei;

import com.mojang.math.Axis;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class ChargingAssemblySubCategory extends SequencedAssemblySubCategory {

    public ChargingAssemblySubCategory() {
        super(25);
    }

    @Override
    public void draw(SequencedRecipe<?> sequencedRecipe, GuiGraphics gg, double mouseX, double mouseY, int index) {
        var ms = gg.pose();
        ms.pushPose();
        ms.translate(-5, 50, 0);
        ms.scale(.6f, .6f, .6f);
        ms.translate(getWidth() / 2, 0, 200);
        ms.mulPose(Axis.XP.rotationDegrees(-15.5f));
        ms.mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(CABlocks.TESLA_COIL.getDefaultState()
                        .setValue(TeslaCoilBlock.FACING, Direction.UP)
                        .setValue(TeslaCoilBlock.POWERED, true))
                .scale(24)
                .render(gg);
        ms.popPose();
    }
}
