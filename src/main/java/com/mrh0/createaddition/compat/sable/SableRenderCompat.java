package com.mrh0.createaddition.compat.sable;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class SableRenderCompat {
    private static final int WIRE_BUFFER_SIZE = 768 * 1024;
    private static final ThreadLocal<MultiBufferSource.BufferSource> WIRE_BUFFER =
            ThreadLocal.withInitial(() -> MultiBufferSource.immediate(new ByteBufferBuilder(WIRE_BUFFER_SIZE)));

    public static boolean renderConnectorBlockEntity(BlockEntity be, PoseStack poseStack,
            float partialTick, double cameraX, double cameraY, double cameraZ,
            BlockEntityRenderDispatcher dispatcher) {
        if (!needsCustomRender(be)) return false;

        MultiBufferSource.BufferSource buf = WIRE_BUFFER.get();
        BlockPos pos = be.getBlockPos();

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
        dispatcher.render(be, partialTick, poseStack, buf);
        buf.endBatch();
        poseStack.popPose();
        return true;
    }

    private static boolean needsCustomRender(BlockEntity be) {
        Level level = be.getLevel();
        if (level == null || !(be instanceof ISableRender)) return false;
        return SableUtil.isInSubLevel(level, be.getBlockPos());
    }
}
