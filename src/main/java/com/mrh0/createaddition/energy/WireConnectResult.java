package com.mrh0.createaddition.energy;

import net.minecraft.util.text.*;

public enum WireConnectResult {
	
	LINKED(new TranslationTextComponent("statusbar.createaddition.wire.linked")),
	LINKED_IN(new TranslationTextComponent("statusbar.createaddition.wire.linked_in")),
	LINKED_OUT(new TranslationTextComponent("statusbar.createaddition.wire.linked_out")),
	
	CONNECT(new TranslationTextComponent("statusbar.createaddition.wire.connect")),
	CONNECT_IN(new TranslationTextComponent("statusbar.createaddition.wire.connect_in")),
	CONNECT_OUT(new TranslationTextComponent("statusbar.createaddition.wire.connect_out")),
	
	LONG(new TranslationTextComponent("statusbar.createaddition.wire.long")),
	OBSTRUCTED(new TranslationTextComponent("statusbar.createaddition.wire.obstructed")),
	COUNT(new TranslationTextComponent("statusbar.createaddition.wire.count")),
	REMOVED(new TranslationTextComponent("statusbar.createaddition.wire.removed")),
	EXISTS(new TranslationTextComponent("statusbar.createaddition.wire.exists")),
	NO_CONNECTION(new TranslationTextComponent("statusbar.createaddition.wire.no_connection")),
	INVALID(new TranslationTextComponent("statusbar.createaddition.wire.invalid"))
	;
	
	private final ITextComponent message;
	
	private WireConnectResult(ITextComponent message) {
		this.message = message;
	}
	
	public ITextComponent getMessage() {
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
