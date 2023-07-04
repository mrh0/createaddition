package com.mrh0.createaddition.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.text.MessageFormat;

public class CADebugger {

	public CADebugger() {}

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

	public static void print(Level level, Object message, Object... args) {
		String side = level == null ? "???" : level.isClientSide ? "CLIENT" : "SERVER";
		if (message == null) message = "null";
		print("[" + side + "] " + message, args);
	}

	public static void printServer(Level level, Object message, Object... args) {
		String side;
		if (level != null && level.isClientSide) return;
		side = level == null ? "?SERVER?" : "SERVER";
		if (message == null) message = "null";
		print("[" + side + "] " + message, args);
	}

	public static void printClient(Level level, Object message, Object... args) {
		String side;
		if (level != null && !level.isClientSide) return;
		side = level == null ? "?CLIENT?" : "CLIENT";
		if (message == null) message = "null";
		print("[" + side + "] " + message, args);
	}

	private static void print(String message, Object... args) {
		int i = 0;
		while (message.contains("{}")) message = message.replaceFirst("\\{}", "{" + i++ + "}");
		System.out.println(MessageFormat.format(message, args));
	}

}
