package com.mrh0.createaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @WrapOperation(
            method = "canCatchFire(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isFlammable(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
            )
    )
    private static boolean igniteFluid(BlockState instance, BlockGetter level, BlockPos blockPos, Direction direction, Operation<Boolean> original) {
        boolean isFlammableBlock = original.call(instance, level, blockPos, direction);
        if (isFlammableBlock) {
            return true;
        }

        FluidState fluidState = level.getFluidState(blockPos);
        return fluidState.is(CATagRegister.Fluids.IGNITES);
    }
}
