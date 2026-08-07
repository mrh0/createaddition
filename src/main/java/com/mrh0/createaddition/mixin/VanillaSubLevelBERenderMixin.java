package com.mrh0.createaddition.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.sable.SableRenderCompat;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer")
public class VanillaSubLevelBERenderMixin {

    @Shadow
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Inject(method = "renderSingleBE", at = @At("HEAD"), cancellable = true, remap = false)
    private void ca$onRenderSingleBE(BlockEntity be, PoseStack poseStack, float partialTick,
                                     double cameraX, double cameraY, double cameraZ,
                                     CallbackInfo ci) {
        if (!CreateAddition.SABLE_ACTIVE) return;
        if (SableRenderCompat.renderConnectorBlockEntity(be, poseStack, partialTick, cameraX, cameraY, cameraZ, blockEntityRenderDispatcher)) {
            ci.cancel();
        }
    }
}
