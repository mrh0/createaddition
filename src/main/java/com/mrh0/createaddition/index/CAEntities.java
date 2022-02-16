package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;

public class CAEntities {
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate();
	
	
	/*public static final EntityEntry<OverchargedHammerEntity> OVERCHARGED_HAMMER_ENTITY =
			register("super_glue", OverchargedHammerEntity::new, () -> OverchargedHammerRenderer::new, MobCategory.MISC, 10,
				40, true, true, OverchargedHammerEntity::build).register();*/
	
	private static <T extends Entity> CreateEntityBuilder<T, ?> register(String name, EntityFactory<T> factory,
			NonNullSupplier<NonNullFunction<Context, EntityRenderer<? super T>>> renderer,
			MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
			NonNullConsumer<FabricEntityTypeBuilder<T>> propertyBuilder) {
		String id = Lang.asId(name);
		return (CreateEntityBuilder<T, ?>) 
			REGISTRATE.entity(id, factory, group)
			.properties(b -> b.trackRangeChunks(range)
				.trackedUpdateRate(updateFrequency)
				.forceTrackedVelocityUpdates(sendVelocity))
			.properties(propertyBuilder)
			.properties(b -> {
				if (immuneToFire)
					b.fireImmune();
			})
			.renderer(renderer);
	}
}
