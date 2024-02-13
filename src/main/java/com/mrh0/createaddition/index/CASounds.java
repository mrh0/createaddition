package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CASounds {
    public static final LazyRegistrar<SoundEvent> SOUND_EVENTS =
            LazyRegistrar.create(Registry.SOUND_EVENT, CreateAddition.MODID);

    public static final Supplier<SoundEvent> ELECTRIC_MOTOR_BUZZ = registerSoundEvent("electric_motor_buzz");
    public static final Supplier<SoundEvent> TESLA_COIL = registerSoundEvent("tesla_coil");
    public static final Supplier<SoundEvent> ELECTRIC_CHARGE = registerSoundEvent("electric_charge");
    public static final Supplier<SoundEvent> LOUD_ZAP = registerSoundEvent("loud_zap");
    public static final Supplier<SoundEvent> LITTLE_ZAP = registerSoundEvent("little_zap");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(CreateAddition.MODID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }

    public static void register() {
        SOUND_EVENTS.register();
    }
}
