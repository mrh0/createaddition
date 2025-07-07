package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CAArmInteractions {
    //private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
    //    T type = factory.apply();
    //    ArmInteractionPointType.register(type);
    //   return type;
    //}

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
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            ItemStack input = stack.copy();
            InteractionResultHolder<ItemStack> res =
                    LiquidBlazeBurnerBlock.tryInsert(cachedState, level, pos, input, false, false, simulate);
            ItemStack remainder = res.getObject();
            if (input.isEmpty()) {
                return remainder;
            } else {
                if (!simulate) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), remainder);
                return input;
            }
        }
    }

    public static void register() {}
}
