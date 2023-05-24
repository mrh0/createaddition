package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.config.Config;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PortableEnergyManager {

	private static final Map<UUID, EnergyStorageHolder> CONTRAPTIONS = new ConcurrentHashMap<>();

	private static int tick = 0;
	public static void tick() {

		CONTRAPTIONS.keySet().iterator().forEachRemaining(contraption -> {
			// It's hard to find out when a contraption is removed...
			// It might be easier and cleaner to just use mixin, but it works.
			if (System.currentTimeMillis() - CONTRAPTIONS.get(contraption).heartbeat > 5_000)
				CONTRAPTIONS.remove(contraption);
		});
	}

	public static void track(MovementContext context) {
		Contraption contraption = context.contraption;
		EnergyStorageHolder holder = CONTRAPTIONS.get(contraption.entity.getUUID());
		if (holder == null) {
			holder = new EnergyStorageHolder();
			CONTRAPTIONS.put(contraption.entity.getUUID(), holder);
		}
		holder.addEnergySource(context.blockEntityData, context.localPos);
	}

	public static void untrack(MovementContext context) {
		EnergyStorageHolder holder = CONTRAPTIONS.remove(context.contraption.entity.getUUID());
		if (holder == null) return;
		holder.removed = true;
	}

	public static @Nullable IEnergyStorage get(Contraption contraption) {
		if (contraption.entity == null) return null;
		return CONTRAPTIONS.get(contraption.entity.getUUID());
	}

	public static class EnergyStorageHolder implements IEnergyStorage {

		private int energy = 0;
		private int capacity = 0;
		private long heartbeat;
		private boolean removed = false;

		private final int maxReceive = Config.ACCUMULATOR_MAX_INPUT.get();
		private final int maxExtract = Config.ACCUMULATOR_MAX_OUTPUT.get();
		private final Map<BlockPos, EnergyData> energyHolders = new HashMap<>();

		public EnergyStorageHolder() {
			this.heartbeat = System.currentTimeMillis();
		}

		protected void addEnergySource(CompoundTag nbt, BlockPos pos) {
			// Heartbeat
			this.heartbeat = System.currentTimeMillis();

			// Make sure this is a controller.
			if (!nbt.contains("EnergyContent")) return;
			// Check for duplicates.
			if (this.energyHolders.containsKey(pos)) return;
			EnergyData data = new EnergyData(nbt);
			this.energy += data.energy;
			this.capacity += data.capacity;
			this.energyHolders.put(pos, data);
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!this.canReceive()) return 0;
			int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
			if (!simulate) {
				this.energy += energyReceived;
				// Store NBT
				int energyLeft = energyReceived;
				for (EnergyData data : energyHolders.values()) {
					energyLeft -= data.receiveEnergy(energyLeft);
					if (energyLeft <= 0) break; // It shouldn't be possible to go below 0, but just in case.
				}
				// In case we didn't store all the energy.
				if (energyLeft > 0) throw new IllegalStateException("Failed to store energy.");
			}
			return energyReceived;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!this.canExtract()) return 0;
			int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
			if (!simulate) {
				this.energy -= energyExtracted;
				// Store NBT
				int energyLeft = energyExtracted;
				for (EnergyData data : energyHolders.values()) {
					energyLeft -= data.extractEnergy(energyLeft);
					if (energyLeft <= 0) break; // It shouldn't be possible to go below 0, but just in case.
				}
				// In case we didn't store all the energy.
				if (energyLeft > 0) throw new IllegalStateException("Failed to store energy.");
			}
			return energyExtracted;
		}

		@Override
		public int getEnergyStored() {
			return this.energy;
		}

		@Override
		public int getMaxEnergyStored() {
			return this.capacity;
		}

		@Override
		public boolean canExtract() {
			return !this.removed;
		}

		@Override
		public boolean canReceive() {
			return !this.removed;
		}
	}

	public static class EnergyData {

		private final CompoundTag nbt;
		private final int capacity;
		private int energy;

		public EnergyData(CompoundTag nbt) {
			CompoundTag energyContent = (CompoundTag)nbt.get("EnergyContent");
			if (energyContent == null) throw new IllegalArgumentException("EnergyContent is null");
			this.nbt = nbt;
			this.capacity = nbt.getInt("EnergyCapacity");
			this.energy = energyContent.getInt("energy");
		}

		public int receiveEnergy(int energy) {
			int energyReceived = Math.min(this.capacity - this.energy, energy);
			if (energyReceived == 0) return 0; // No need to save if nothing changed.
			this.energy += energyReceived;

			// Save
			CompoundTag energyContent = (CompoundTag)nbt.get("EnergyContent");
			energyContent.putInt("energy", this.energy);

			return energyReceived;
		}

		public int extractEnergy(int energy) {
			int energyRemoved = Math.min(this.energy, energy);
			if (energyRemoved == 0) return 0; // No need to save if nothing changed.
			this.energy -= energyRemoved;

			// Save
			CompoundTag energyContent = (CompoundTag)nbt.get("EnergyContent");
			energyContent.putInt("energy", this.energy);

			return energyRemoved;
		}
	}

}
