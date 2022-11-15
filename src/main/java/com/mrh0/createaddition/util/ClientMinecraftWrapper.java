package com.mrh0.createaddition.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientMinecraftWrapper {
	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}
}
