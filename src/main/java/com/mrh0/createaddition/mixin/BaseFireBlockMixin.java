package com.mrh0.createaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {

    @WrapOperation(
            method = "canBePlacedAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"
            )
    )
    private static boolean canPlaceFire(BlockState state, Operation<Boolean> original) {
        boolean isAir = original.call(state);
        FluidState fluid = state.getFluidState();
        if (fluid.isEmpty()) {
            return isAir;
        }

        boolean ignites = fluid.is(CATagRegister.Fluids.IGNITES);

        return isAir || ignites;
    }
}
