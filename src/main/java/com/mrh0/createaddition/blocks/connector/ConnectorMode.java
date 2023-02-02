package com.mrh0.createaddition.blocks.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

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
	
	public static ConnectorMode test(Level level, BlockPos pos, Direction face) {
		BlockEntity be = level.getBlockEntity(pos);
		if(be == null) return None;
		LazyOptional<IEnergyStorage> optional = be.getCapability(CapabilityEnergy.ENERGY, face);
		if(!optional.isPresent()) return None;
		if(optional.orElse(null) == null) return None;
		
		IEnergyStorage e = optional.orElse(null);
		
		if(e.canExtract() && e.canReceive()) return Passive;
		if(e.canExtract()) return Pull;
		if(e.canReceive()) return Push;
		
		return None;
	}
}