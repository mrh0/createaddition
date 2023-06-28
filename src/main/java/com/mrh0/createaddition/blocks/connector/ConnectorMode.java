package com.mrh0.createaddition.blocks.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public enum ConnectorMode implements StringRepresentable {
	Push("push"),
	Pull("pull"),
	None("none"),
	Passive("passive");

	private String name;
	
	private ConnectorMode(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public ConnectorMode getNext() {
		switch (this) {
			case Passive:
				return None;
			case None:
				return Pull;
			case Pull:
				return Push;
			case Push:
				return Passive;
		}
		return None;
	}
	
	public MutableComponent getTooltip() {
		switch (this) {
			case Passive:
				return new TranslatableComponent("createaddition.tooltip.energy.passive");
			case None:
				return new TranslatableComponent("createaddition.tooltip.energy.none");
			case Pull:
				return new TranslatableComponent("createaddition.tooltip.energy.pull");
			case Push:
				return new TranslatableComponent("createaddition.tooltip.energy.push");
		}
		return new TranslatableComponent("createaddition.tooltip.energy.none");
	}
	
	public boolean isActive() {
		return this == Push || this == Pull;
	}
	
	public static ConnectorMode test(Level level, BlockPos pos, Direction face) {
		EnergyStorage e = EnergyStorage.SIDED.find(level, pos, face);
		if(e == null) return None;
		
		if(e.supportsExtraction() && e.supportsInsertion()) return Passive;
		if(e.supportsExtraction()) return Pull;
		if(e.supportsInsertion()) return Push;
		
		return None;
	}
}