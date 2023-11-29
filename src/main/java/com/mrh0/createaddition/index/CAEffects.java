package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.effect.ShockingEffect;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;


public class CAEffects {
	public static final LazyRegistrar<MobEffect> EFFECTS = LazyRegistrar.create(Registries.MOB_EFFECT, CreateAddition.MODID);
	public static final RegistryObject<MobEffect> SHOCKING = EFFECTS.register("shocking", () -> new ShockingEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "6ed2d177-af97-423c-84f5-1f80c364639f", (double)-100f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	
	public static void register() {
		EFFECTS.register();
	}
}
