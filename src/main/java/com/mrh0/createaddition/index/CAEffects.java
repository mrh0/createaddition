package com.mrh0.createaddition.index;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.effect.ShockingEffect;


public class CAEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CreateAddition.MODID);
	public static final RegistryObject<MobEffect> SHOCKING = EFFECTS.register("shocking", () -> new ShockingEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "6ed2d177-af97-423c-84f5-1f80c364639f", (double)-100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	
	public static void register(IEventBus bus) {
		EFFECTS.register(bus);
	}
}
