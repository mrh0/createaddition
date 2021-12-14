package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.entities.overcharged_hammer.OverchargedHammerEntity;
import com.mrh0.createaddition.entities.overcharged_hammer.OverchargedHammerRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.RegistryEntry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;

public class CAEntities {
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate();
	
	public static final RegistryEntry<EntityType<OverchargedHammerEntity>> OVERCHARGED_HAMMER_ENTITY = 
			register("overcharged_hammer", OverchargedHammerEntity::new, MobCategory.MISC);
	
	public static <T extends Entity> RegistryEntry<EntityType<T>> register(String name, EntityFactory<T> factory, MobCategory group) {
		return REGISTRATE.entity(name, factory, group)
				.properties(b -> b.setTrackingRange(10)
					.setUpdateInterval(10)
					.setShouldReceiveVelocityUpdates(true))
				.properties(OverchargedHammerEntity::build)
				.properties(b -> {
					b.fireImmune();
				})
				.register();
	}
	
	public static void register() {}
	
	public static void registerRenderers() {
		System.out.println("REGCLIENT");
		RenderingRegistry.registerEntityRenderingHandler(OVERCHARGED_HAMMER_ENTITY.get(), OverchargedHammerRenderer::new);
	}
}
