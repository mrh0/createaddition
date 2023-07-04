package com.mrh0.createaddition.index;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;

public class CAPotatoCannonProjectiles {
	
	public static final PotatoCannonProjectileType
		CHOCOLATE_CAKE = create("chocolate_cake")
			.damage(8)
			.reloadTicks(15)
			.knockback(0.1f)
			.velocity(1.1f)
			.renderTumbling()
			.sticky()
			.soundPitch(1.0f)
			.registerAndAssign(CABlocks.CHOCOLATE_CAKE.get()),
		HONEY_CAKE = create("honey_cake")
			.damage(8)
			.reloadTicks(15)
			.knockback(0.1f)
			.velocity(1.1f)
			.renderTumbling()
			.sticky()
			.soundPitch(1.0f)
			.registerAndAssign(CABlocks.HONEY_CAKE.get());
	
	public static void register() {
		
	}
	
	private static PotatoCannonProjectileType.Builder create(String name) {
		return new PotatoCannonProjectileType.Builder(Create.asResource(name));
	}
}
