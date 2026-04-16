package com.mrh0.createaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    @WrapMethod(
            method = "canSpreadTo(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/material/Fluid;)Z"
    )
    private boolean protectFire(
            BlockGetter level,
            BlockPos fromPos,
            BlockState fromBlockState,
            Direction direction,
            BlockPos toPos,
            BlockState toBlockState,
            FluidState toFluidState,
            Fluid fluid,
            Operation<Boolean> original
    ) {
        boolean canSpreadTo = original.call(level, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluid);
        if (!canSpreadTo) {
            return false;
        }

        if (direction == Direction.DOWN) {
            return true;
        }

        boolean ignites = fluid.is(CATagRegister.Fluids.IGNITES);
        if (!ignites) {
            return true;
        }

        return !toBlockState.is(BlockTags.FIRE);
    }

    @WrapOperation(
            method = "spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean spreadFire(
            LevelAccessor level,
            BlockPos blockPos,
            BlockState fluidBlockState,
            int i,
            Operation<Boolean> original,
            LevelAccessor duplicate1,
            BlockPos duplicate2,
            BlockState toReplace,
            Direction flowDirection,
            FluidState fluidState
    ) {
        boolean result = original.call(level, blockPos, fluidBlockState, i);
        if (level instanceof Level) {
            if (!((Level) level).getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
                return result;
            }
        }


        if (flowDirection == Direction.DOWN && fluidState.is(CATagRegister.Fluids.IGNITES) && toReplace.is(BlockTags.FIRE)) {
            Direction.Plane.HORIZONTAL.stream().forEach(direction -> {
                BlockPos side = blockPos.relative(direction);
                if (level.isEmptyBlock(side) && toReplace.canSurvive(level, side)) {
                    level.setBlock(side, toReplace, 3);
                }
            });
        }
        return result;
    }
}
