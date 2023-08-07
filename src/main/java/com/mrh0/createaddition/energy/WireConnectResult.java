package com.mrh0.createaddition.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum WireConnectResult {
	
	LINKED(new TranslatableComponent("statusbar.createaddition.wire.linked")),
	LINKED_IN(new TranslatableComponent("statusbar.createaddition.wire.linked_in")),
	LINKED_OUT(new TranslatableComponent("statusbar.createaddition.wire.linked_out")),
	
	CONNECT(new TranslatableComponent("statusbar.createaddition.wire.connect")),
	CONNECT_IN(new TranslatableComponent("statusbar.createaddition.wire.connect_in")),
	CONNECT_OUT(new TranslatableComponent("statusbar.createaddition.wire.connect_out")),
	
	LONG(new TranslatableComponent("statusbar.createaddition.wire.long").withStyle(ChatFormatting.RED)),
	OBSTRUCTED(new TranslatableComponent("statusbar.createaddition.wire.obstructed").withStyle(ChatFormatting.RED)),
	COUNT(new TranslatableComponent("statusbar.createaddition.wire.count").withStyle(ChatFormatting.RED)),
	REMOVED(new TranslatableComponent("statusbar.createaddition.wire.removed")),
	EXISTS(new TranslatableComponent("statusbar.createaddition.wire.exists").withStyle(ChatFormatting.RED)),
	NO_CONNECTION(new TranslatableComponent("statusbar.createaddition.wire.no_connection").withStyle(ChatFormatting.RED)),
	INVALID(new TranslatableComponent("statusbar.createaddition.wire.invalid").withStyle(ChatFormatting.RED)),
	REQUIRES_HIGH_CURRENT(new TranslatableComponent("statusbar.createaddition.wire.requires_high_current").withStyle(ChatFormatting.RED))
	;
	
	private final Component message;
	
	private WireConnectResult(Component message) {
		this.message = message;
	}
	
	public Component getMessage() {
		return message;
	}
	
	public boolean isLinked() {
		return this == LINKED || this == LINKED_IN || this == LINKED_OUT;
	}
	
	public boolean isConnect() {
		return this == CONNECT || this == CONNECT_IN || this == CONNECT_OUT;
	}
	
	public static WireConnectResult getLink(boolean in, boolean out) {
		if(in && !out)
			return LINKED_IN;
		if(!in && out)
			return LINKED_OUT;
		return LINKED;
	}
	
	public static WireConnectResult getConnect(boolean in, boolean out) {
		if(in && !out)
			return CONNECT_IN;
		if(!in && out)
			return CONNECT_OUT;
		return CONNECT;
	}
}
