package com.mrh0.createaddition.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.blocks.tesla_coil.TeslaCoilBlock;
import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.foundation.gui.CustomLightingSettings;
import com.simibubi.create.foundation.gui.ILightingSettings;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class CAEmiAnimations {
    public static final ILightingSettings DEFAULT_LIGHTING = CustomLightingSettings.builder()
            .firstLightRotation(12.5f, 45.0f)
            .secondLightRotation(-20.0f, 50.0f)
            .build();


    public static void addRoller(WidgetHolder widgets, int x, int y, int offset) {
        widgets.addDrawable(x, y, 0, 0, (graphics, mouseX, mouseY, delta) -> {
            renderRoller(graphics, offset);
        });
    }

    public static void renderRoller(GuiGraphics graphics, int offset) {
        PoseStack matrices = graphics.pose();
        matrices.translate(-5, offset + 16, 200);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(22.5f));
        int scale = 25;

        CreateEmiAnimations.blockElement(CABlocks.ROLLING_MILL.getDefaultState())
                .rotateBlock(0, 0, 0)
                .scale(scale)
                .render(graphics);

        CreateEmiAnimations.blockElement(CreateEmiAnimations.shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, CreateEmiAnimations.getCurrentAngle())
                .scale(scale)
                .atLocal(0, -0.2, 0)
                .render(graphics);

        CreateEmiAnimations.blockElement(CreateEmiAnimations.shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, -CreateEmiAnimations.getCurrentAngle())
                .scale(scale)
                .atLocal(0, 0, 0)
                .render(graphics);
    }


    public static void addTeslaCoil(WidgetHolder widgets, int x, int y, Direction dir, boolean depot, int offset) {
        widgets.addDrawable(x, y, 0, 0, (graphics, mouseX, mouseY, delta) -> {
            renderTeslaCoil(graphics, offset, dir, depot);
        });
    }

    public static void renderTeslaCoil(GuiGraphics graphics, int offset, Direction dir, boolean depot) {
        PoseStack matrices = graphics.pose();
        matrices.translate(offset + 2, 3, 200);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(22.5f));
        int scale = 25;

        CreateEmiAnimations.blockElement(CABlocks.TESLA_COIL.getDefaultState()
                        .setValue(TeslaCoilBlock.FACING, dir)
                        .setValue(TeslaCoilBlock.POWERED, true))
                .scale(scale)
                .render(graphics);

        if (depot) {
            CreateEmiAnimations.blockElement(AllBlocks.DEPOT.getDefaultState())
                    .atLocal(0, 2, 0)
                    .scale(scale)
                    .render(graphics);
        }
    }
}