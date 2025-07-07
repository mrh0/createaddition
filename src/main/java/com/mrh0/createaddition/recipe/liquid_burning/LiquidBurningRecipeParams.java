package com.mrh0.createaddition.recipe.liquid_burning;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class LiquidBurningRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<LiquidBurningRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(LiquidBurningRecipeParams::new).forGetter(Function.identity())
    ).apply(instance, (params) -> params));
    public static StreamCodec<RegistryFriendlyByteBuf, LiquidBurningRecipeParams> STREAM_CODEC = streamCodec(LiquidBurningRecipeParams::new);

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
    }
}
