package com.mrh0.createaddition.debug;

import com.simibubi.create.content.contraptions.KineticDebugger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Basically a copy of creates {@link KineticDebugger}, but for create addition blocks.
 */
public class AdditionDebugger {

	public AdditionDebugger() {}

	public static void tick() {
		if (!Minecraft.getInstance().options.renderDebug) return;
		IDebugDrawer drawer = getSelected();
		if (drawer == null) return;
		drawer.drawDebug();
	}

	public static IDebugDrawer getSelected() {
		HitResult result = Minecraft.getInstance().hitResult;
		ClientLevel world = Minecraft.getInstance().level;
		if (result == null || world == null) return null;
		if (!(result instanceof BlockHitResult res)) return null;
		BlockEntity entity = world.getBlockEntity(res.getBlockPos());
		if (entity instanceof IDebugDrawer drawer) return drawer;
		return null;
	}

}
