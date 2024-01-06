package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CASounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CreateAddition.MODID);

    public static final RegistryObject<SoundEvent> ELECTRIC_MOTOR_BUZZ = registerSoundEvent("electric_motor_buzz");
    public static final RegistryObject<SoundEvent> TESLA_COIL = registerSoundEvent("tesla_coil");
    public static final RegistryObject<SoundEvent> LOUD_ZAP = registerSoundEvent("loud_zap");
    public static final RegistryObject<SoundEvent> LITTLE_ZAP = registerSoundEvent("little_zap");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(CreateAddition.MODID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
