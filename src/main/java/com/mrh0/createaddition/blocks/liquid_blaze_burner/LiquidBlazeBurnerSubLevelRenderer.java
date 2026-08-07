package com.mrh0.createaddition.blocks.liquid_blaze_burner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;

public final class LiquidBlazeBurnerSubLevelRenderer {
    private LiquidBlazeBurnerSubLevelRenderer() {}

    public static void render(LiquidBlazeBurnerBlockEntity be, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource) {
        BlazeBurnerBlock.HeatLevel heatLevel = be.getHeatLevelFromBlock();
        if (heatLevel == BlazeBurnerBlock.HeatLevel.NONE) return;

        float animation = be.headAnimation.getValue(partialTick) * .175f;
        float horizontalAngle = AngleHelper.rad(be.headAngle.getValue(partialTick));
        boolean canDrawFlame = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
        boolean drawGoggles = be.goggles;
        int hashCode = be.hashCode();

        LiquidBlazeBurnerRenderer.renderShared(poseStack, null, bufferSource,
                be.getLevel(), be.getBlockState(), heatLevel,
                animation, horizontalAngle, canDrawFlame, drawGoggles, hashCode);
    }
}
