package com.mrh0.createaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mrh0.createaddition.datagen.TagProvider.CATagRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {

    @Unique
    private void ca_ignite(Level level, BlockPos pos, FluidState state) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }

        if (!state.is(CATagRegister.Fluids.IGNITES)) {
            return;
        }

        BlockPos.MutableBlockPos mutable = pos.mutable();
        Direction fireFace = Direction.Plane.HORIZONTAL.stream().filter(adjacent -> {
            mutable.setWithOffset(pos, adjacent);
            return level.getBlockState(mutable).is(BlockTags.FIRE);
        }).findAny().orElse(null);

        if (fireFace == null) {
            return;
        }

        BlockPos firePos = pos.relative(fireFace);
        BlockState fire = level.getBlockState(firePos);
        if (fire.canSurvive(level, firePos)) {
            level.setBlockAndUpdate(pos, fire);
            level.playSound(
                    null,
                    pos,
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS,
                    0.5F,
                    2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
            );
        }
    }

    @Inject(
            method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("RETURN")
    )
    private void injectIgnite(Level level, BlockPos pos, FluidState state, CallbackInfo ci) {
        ca_ignite(level, pos, state);
    }

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
    private boolean propagateFireUp(
            LevelAccessor level,
            BlockPos blockPos,
            BlockState fluidBlockState,
            int i,
            Operation<Boolean> original,
            LevelAccessor duplicate1,
            BlockPos duplicate2,
            BlockState toReplace,
            Direction direction,
            FluidState fluidState
    ) {
        boolean result = original.call(level, blockPos, fluidBlockState, i);
        if (level instanceof Level) {
            if (!((Level) level).getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
                return result;
            }
        }


        if (direction == Direction.DOWN && fluidState.is(CATagRegister.Fluids.IGNITES) && toReplace.is(BlockTags.FIRE)) {
            BlockPos fluidPos = blockPos.above();
            level.setBlock(fluidPos, toReplace, 3);
        }
        return result;
    }
}
