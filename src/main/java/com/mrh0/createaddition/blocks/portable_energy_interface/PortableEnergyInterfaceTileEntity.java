package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.simibubi.create.content.contraptions.components.actors.PortableStorageInterfaceTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class PortableEnergyInterfaceTileEntity extends PortableStorageInterfaceTileEntity {

	protected LazyOptional<IEnergyStorage> capability = this.createEmptyHandler();

	public PortableEnergyInterfaceTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void startTransferringTo(Contraption contraption, float distance) {
		LazyOptional<IEnergyStorage> oldcap = this.capability;
		this.capability = LazyOptional.of(() -> new InterfaceEnergyHandler(PortableEnergyManager.get(contraption)));
		oldcap.invalidate();
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void invalidateCapability() {
		this.capability.invalidate();
	}

	@Override
	protected void stopTransferring() {
		LazyOptional<IEnergyStorage> oldcap = this.capability;
		this.capability = this.createEmptyHandler();
		oldcap.invalidate();
		super.stopTransferring();
	}

	private LazyOptional<IEnergyStorage> createEmptyHandler() {
		return LazyOptional.of(() -> new InterfaceEnergyHandler(new EnergyStorage(0)));
	}

	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
		return cap == CapabilityEnergy.ENERGY ? this.capability.cast() : super.getCapability(cap, side);
	}

	// Implement protected methods.

	protected boolean isConnected() {
		int timeUnit = this.getTransferTimeout();
		return this.transferTimer >= 4 && this.transferTimer <= timeUnit + 4;
	}

	protected float getExtensionDistance(float partialTicks) {
		return (float)(Math.pow(this.connectionAnimation.getValue(partialTicks), 2.0D) * (double)this.distance / 2.0D);
	}

	protected float getConnectionDistance() {
		return this.distance;
	}

	protected Entity getConnectedEntity() {
		return this.connectedEntity;
	}

	protected int getTransferTimer() {
		return this.transferTimer;
	}

	public class InterfaceEnergyHandler implements IEnergyStorage {

		private final IEnergyStorage wrapped;

		public InterfaceEnergyHandler(IEnergyStorage wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!PortableEnergyInterfaceTileEntity.this.canTransfer()) return 0;
			int received = this.wrapped.receiveEnergy(maxReceive, simulate);
			if (received != 0 && !simulate) this.keepAlive();
			return received;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!PortableEnergyInterfaceTileEntity.this.canTransfer()) return 0;
			int extracted = this.wrapped.extractEnergy(maxExtract, simulate);
			if (extracted != 0 && !simulate) this.keepAlive();
			return extracted;
		}

		@Override
		public int getEnergyStored() {
			return this.wrapped.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return this.wrapped.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return this.wrapped.canExtract();
		}

		@Override
		public boolean canReceive() {
			return this.wrapped.canReceive();
		}

		public void keepAlive() {
			PortableEnergyInterfaceTileEntity.this.onContentTransferred();
		}
	}
}
