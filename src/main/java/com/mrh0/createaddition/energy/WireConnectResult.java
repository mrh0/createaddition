package com.mrh0.createaddition.energy;

import net.minecraft.network.chat.Component;

public enum WireConnectResult {
	
	LINKED(Component.translatable("statusbar.createaddition.wire.linked")),
	LINKED_IN(Component.translatable("statusbar.createaddition.wire.linked_in")),
	LINKED_OUT(Component.translatable("statusbar.createaddition.wire.linked_out")),
	
	CONNECT(Component.translatable("statusbar.createaddition.wire.connect")),
	CONNECT_IN(Component.translatable("statusbar.createaddition.wire.connect_in")),
	CONNECT_OUT(Component.translatable("statusbar.createaddition.wire.connect_out")),
	
	LONG(Component.translatable("statusbar.createaddition.wire.long")),
	OBSTRUCTED(Component.translatable("statusbar.createaddition.wire.obstructed")),
	COUNT(Component.translatable("statusbar.createaddition.wire.count")),
	REMOVED(Component.translatable("statusbar.createaddition.wire.removed")),
	EXISTS(Component.translatable("statusbar.createaddition.wire.exists")),
	NO_CONNECTION(Component.translatable("statusbar.createaddition.wire.no_connection")),
	INVALID(Component.translatable("statusbar.createaddition.wire.invalid"))
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
