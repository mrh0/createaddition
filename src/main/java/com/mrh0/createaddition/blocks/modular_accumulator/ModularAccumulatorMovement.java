package com.mrh0.createaddition.blocks.modular_accumulator;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;

public class ModularAccumulatorMovement implements MovementBehaviour {

	/**
	 * The delay between each tick.
	 */
	public static final int TICK_DELAY = 20;

	@Override
	public void tick(MovementContext context) {
		if (context.contraption.entity == null) return;
		if (context.world.isClientSide) return;
		// Try to add the accumulator each tick / every TICK_DELAY, this fixes multiple
		// issues, such as there not being a way to detect when a contraption is loaded
		// or when a contraption is unloaded.
		TemporaryData data = (TemporaryData) context.temporaryData;
		if (data == null) {
			// The contraption / entity was just loaded.
			data = new TemporaryData();
			context.temporaryData = data;

			CompoundTag nbt = context.blockEntityData;
			data.controller = nbt.contains("EnergyContent");
			data.tick = TICK_DELAY;
		}

		if (!data.controller) return;
		if (data.tick >= TICK_DELAY) {
			data.tick = 0;
			// This either adds the accumulator, or acts as a heartbeat for the tracked contraption.
			PortableEnergyManager.track(context);
		} else data.tick++;
	}

	@Override
	public void startMoving(MovementContext context) {
		// If the contraption didn't actually move (ex: pulley tried to move down, but was blocked)
		// then don't do anything.
		if (context.contraption.entity == null) return;
		if (context.world.isClientSide) return;
		if (!context.blockEntityData.contains("EnergyContent")) return;
		context.temporaryData = new TemporaryData(true);

		PortableEnergyManager.track(context);
	}

	@Override
	public void stopMoving(MovementContext context) {
		if (context.contraption.entity == null) return;
		if (context.world.isClientSide) return;

		PortableEnergyManager.untrack(context);
	}

	@Override
	public boolean renderAsNormalBlockEntity() {
		return true;
	}

	private static class TemporaryData {

		public boolean controller = false;
		private int tick = 0;

		public TemporaryData(boolean controller) {
			this.controller = controller;
		}

		public TemporaryData() {}
	}
}
