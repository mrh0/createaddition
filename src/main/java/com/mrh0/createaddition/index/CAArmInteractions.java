package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class CAArmInteractions {
    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = factory.apply(CreateAddition.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static final LiquidBlazeBurnerType LIQUID_BLAZE_BURNER = register("liquid_blaze_burner", LiquidBlazeBurnerType::new);

    public static class LiquidBlazeBurnerType extends ArmInteractionPointType {
        public LiquidBlazeBurnerType(ResourceLocation id) {
            super(id);
        }

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
            InteractionResultHolder<ItemStack> res =
                    LiquidBlazeBurnerBlock.tryInsert(cachedState, level, pos, input[0], ContainerItemContext.ofSingleSlot(new SingleStackStorage(){
                        @Override
                        protected ItemStack getStack() {
                            return input[0];
                        }
                        @Override
                        protected void setStack(ItemStack stack) {
                            input[0] = stack;
                        }
                    }),false, false, false);
            ItemStack remainder = res.getObject();
            if (input[0].isEmpty()) {
                return remainder;
            } else {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remainder);
                return input[0];
            }
        }
    }

    public static void register() {}
}
