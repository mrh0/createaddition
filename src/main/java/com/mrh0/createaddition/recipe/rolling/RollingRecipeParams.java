package com.mrh0.createaddition.recipe.rolling;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class RollingRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<RollingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(RollingRecipeParams::new).forGetter(Function.identity())
    ).apply(instance, (params) -> params));
    public static StreamCodec<RegistryFriendlyByteBuf, RollingRecipeParams> STREAM_CODEC = streamCodec(RollingRecipeParams::new);

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
    }
}
