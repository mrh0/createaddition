package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.config.Config;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PortableEnergyManager {

	private static final Map<UUID, EnergyStorageHolder> contraptions = new ConcurrentHashMap<>();
	private static int tick = 0;

	public static void tick() {
		tick++;
		if (tick % (20 * 10) == 0) System.out.println("PortableEnergyManager: " + contraptions.size() + " contraptions.");
		contraptions.keySet().iterator().forEachRemaining(contraption -> {
			// It's hard to find out when a contraption is removed...
			// It might be easier and cleaner to just use mixin.
			if (System.currentTimeMillis() - contraptions.get(contraption).heartbeat > 10_000) {
				System.out.println("PortableEnergyManager: Removing timed out contraption.");
				contraptions.remove(contraption);
			}
		});
	}

	public static void add(MovementContext context) {
		Contraption contraption = context.contraption;
		if (contraption.entity == null) return;
		EnergyStorageHolder holder = contraptions.get(contraption.entity.getUUID());
		if (holder == null) {
			holder = new EnergyStorageHolder();
			contraptions.put(contraption.entity.getUUID(), holder);
		}
		holder.addEnergySource(context.tileData, context.localPos);
	}

	public static IEnergyStorage get(Contraption contraption) {
		if (contraption.entity == null) {
			System.out.println("PortableEnergyManager: Contraption has no entity.");
			return null;
		}
		return contraptions.get(contraption.entity.getUUID());
	}

	public static class EnergyStorageHolder implements IEnergyStorage {

		private int energy = 0;
		private int capacity = 0;

		private long heartbeat;

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
			System.out.println("Added 1");
			if (!nbt.contains("EnergyContent")) return;
			// Check for duplicates.
			System.out.println("Added 2");
			if (this.energyHolders.containsKey(pos)) return;
			System.out.println("Added 3");
			EnergyData data = new EnergyData(nbt);
			this.energy += data.energy;
			this.capacity += data.capacity;
			this.energyHolders.put(pos, data);
			System.out.println("PortableEnergyManager: Added energy source. Energy: " + this.energy + ", Cap: " + this.capacity);
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
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
			int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
			//System.out.println("PortableEnergyManager: Extracting " + energyExtracted + " energy. Want: " + maxExtract + "Energy: " + this.energy + ", Cap: " + this.capacity);
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
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
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
			this.energy += energyReceived;

			// Save
			CompoundTag energyContent = (CompoundTag)nbt.get("EnergyContent");
			energyContent.putInt("energy", this.energy);

			return energyReceived;
		}

		public int extractEnergy(int energy) {
			int energyRemoved = Math.min(this.energy, energy);
			this.energy -= energyRemoved;

			// Save
			CompoundTag energyContent = (CompoundTag)nbt.get("EnergyContent");
			energyContent.putInt("energy", this.energy);

			return energyRemoved;
		}
	}

}
