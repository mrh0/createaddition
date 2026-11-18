package com.mrh0.createaddition.blocks.stator;

import net.minecraft.util.StringRepresentable;

public enum StatorType implements StringRepresentable {
	SIDE("side"),
	CORNER("corner");

	private final String name;

	StatorType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
