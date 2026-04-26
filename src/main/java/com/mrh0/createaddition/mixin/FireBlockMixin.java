package com.mrh0.createaddition.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

    @ModifyExpressionValue(
            method = "getIgniteOdds(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)I",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I")
    )
    private int considerFluidFireSpreadSpeed(int igniteOdd, LevelReader level, BlockPos origin, @Local Direction direction) {
        BlockPos fluidPos = origin.relative(direction);

        FluidState fluidState = level.getFluidState(fluidPos);
        if (fluidState.isEmpty()) {
            return igniteOdd;
        }
        if (!fluidState.is(CATagRegister.Fluids.IGNITES)) {
            return igniteOdd;
        }
        int fireSpeed = 75;
        if (level instanceof Level weatherLevel) {
            if (weatherLevel.isRainingAt(fluidPos)) {
                fireSpeed = 100;
            }
        }

        return Math.max(igniteOdd, fireSpeed);
    }

    @ModifyExpressionValue(
            method = "checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFlammability(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I")
    )
    private int considerFluidFlammability(int original, Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (fluidState.isEmpty()) {
            return original;
        }

        if (!fluidState.is(CATagRegister.Fluids.IGNITES)) {
            return original;
        }

        int flammability = 250;
        if (level.isRainingAt(pos)) {
            flammability = 300;
        }

        return Math.max(original, flammability);
    }

    @ModifyExpressionValue(
            method = "tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isFireSource(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean keepFire(
            boolean original,
            @Local(argsOnly = true) ServerLevel level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        if (original) {
            return true;
        }

        return Direction.Plane.HORIZONTAL.stream().anyMatch(direction -> {
            BlockPos fluidPos = pos.relative(direction);
            FluidState fluid = level.getFluidState(fluidPos);

            if (fluid.isEmpty()) {
                return false;
            }

            return fluid.is(CATagRegister.Fluids.IGNITES);
        });
    }
}
