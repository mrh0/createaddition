package com.mrh0.createaddition.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public class ClientMinecraftWrapper {
	@SuppressWarnings("resource")
	public static Level getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@SuppressWarnings("resource")
	public static Font getFont() {
		return Minecraft.getInstance().font;
	}

	public static LocalPlayer getPlayer() {
		return Minecraft.getInstance().player;
	}
}
