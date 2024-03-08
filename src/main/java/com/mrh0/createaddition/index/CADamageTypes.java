package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class CADamageTypes {

    public static final ResourceKey<DamageType>
        BARBED_WIRE = key("barbed_wire"),
        TESLA_COIL = key("tesla_coil");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, CreateAddition.asResource(name));
    }
    public static void bootstrap(BootstapContext<DamageType> ctx) {
        new DamageTypeBuilder(BARBED_WIRE).exhaustion(0.0f).scaling(DamageScaling.ALWAYS).register(ctx);
        new DamageTypeBuilder(TESLA_COIL).exhaustion(0.0f).scaling(DamageScaling.ALWAYS).register(ctx);
    }

    public static void register() {}
}