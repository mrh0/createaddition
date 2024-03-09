package com.mrh0.createaddition.index;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class CADamageSources {
    public static DamageSource barbedWire(Level level){
        return source(CADamageTypes.BARBED_WIRE, level);
    }
    public static DamageSource teslaCoil(Level level){
        return source(CADamageTypes.TESLA_COIL, level);
    }
    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
        Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(registry.getHolderOrThrow(key));
    }
}
