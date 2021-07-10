package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.entities.overcharged_hammer.OverchargedHammerEntity;
import com.mrh0.createaddition.entities.overcharged_hammer.OverchargedHammerRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.RegistryEntry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class CAEntities {
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate();
	
	public static final RegistryEntry<EntityType<OverchargedHammerEntity>> OVERCHARGED_HAMMER_ENTITY = 
			register("overcharged_hammer", OverchargedHammerEntity::new, EntityClassification.MISC);
	
	public static <T extends Entity> RegistryEntry<EntityType<T>> register(String name, IFactory<T> factory, EntityClassification group) {
		return REGISTRATE.entity(name, factory, group)
				.properties(b -> b.setTrackingRange(10)
					.setUpdateInterval(10)
					.setShouldReceiveVelocityUpdates(true))
				.properties(OverchargedHammerEntity::build)
				.properties(b -> {
					b.immuneToFire();
				})
				.register();
	}
	
	public static void register() {}
	
	public static void registerRenderers() {
		System.out.println("REGCLIENT");
		RenderingRegistry.registerEntityRenderingHandler(OVERCHARGED_HAMMER_ENTITY.get(), OverchargedHammerRenderer::new);
	}
}
