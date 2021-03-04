package com.mrh0.createaddition.energy;

import net.minecraft.util.text.*;

public enum WireConnectResult {
	
	CONNECTED(new TranslationTextComponent("statusbar.createaddition.wire.connected")),
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
}
