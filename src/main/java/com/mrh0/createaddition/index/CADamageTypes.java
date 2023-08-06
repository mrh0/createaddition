package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.damageTypes.DamageTypeData;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class CADamageTypes {
    public static final DamageTypeData BARBED_WIRE = DamageTypeData.builder()
            .simpleId("barbed_wire")
            .exhaustion(0.1f)
            .scaling(DamageScaling.ALWAYS)
            .build();

    public static final DamageTypeData TESLA_COIL = DamageTypeData.builder()
            .simpleId("tesla_coil")
            .exhaustion(0.1f)
            .scaling(DamageScaling.ALWAYS)
            .build();

    public static void bootstrap(BootstapContext<DamageType> ctx) {
        DamageTypeData.allInNamespace(CreateAddition.MODID).forEach(data -> data.register(ctx));
    }
}
