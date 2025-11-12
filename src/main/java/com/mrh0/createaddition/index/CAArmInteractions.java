package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CAArmInteractions {
    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateAddition.asResource(name), type);
    }

    static {
        register("liquid_blaze_burner", new LiquidBlazeBurnerType());
    }

    public static class LiquidBlazeBurnerType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return CABlocks.LIQUID_BLAZE_BURNER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new LiquidBlazeBurnerPoint(this, level, pos, state);
        }
    }

    public static class LiquidBlazeBurnerPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
        public LiquidBlazeBurnerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ItemStack stack, TransactionContext t) {
            final ItemStack[] input = {stack.copy()};
            ContainerItemContext context = ContainerItemContext.ofSingleSlot(
                    new SingleStackStorage(){
                        @Override
                        protected ItemStack getStack() {
                            return input[0];
                        }
                        @Override
                        protected void setStack(ItemStack stack) {
                            input[0] = stack;
                        }
                    });
            LiquidBlazeBurnerBlock.tryInsert(cachedState, level, pos, input[0], context, t, false);
            return input[0];
        }
    }

    public static void register() {}
}
