package com.mrh0.createaddition.blocks.portable_energy_interface;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionSuccessCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class PortableEnergyInterfaceTileEntity extends PortableStorageInterfaceBlockEntity implements EnergyTransferable {

	protected EnergyStorage capability = this.createEmptyHandler();

	// Default limits for PortableEnergyManager.
	//public int maxInput = Config.PEI_MAX_INPUT.get();
	//public int maxOutput = Config.PEI_MAX_OUTPUT.get();

	public PortableEnergyInterfaceTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void startTransferringTo(Contraption contraption, float distance) {
		this.capability = new InterfaceEnergyHandler(PortableEnergyManager.get(contraption));
		super.startTransferringTo(contraption, distance);
	}

	@Override
	protected void stopTransferring() {
		this.capability = this.createEmptyHandler();
		super.stopTransferring();
	}

	@Override
	protected void invalidateCapability() {

	}

	private EnergyStorage createEmptyHandler() {
		return new InterfaceEnergyHandler(new SimpleEnergyStorage(0, 0, 0));
	}

	@Override
	public EnergyStorage getEnergyStorage(Direction side) {
		return this.capability;
//		if (CreateAddition.CC_ACTIVE && Peripherals.isPeripheral(cap)) return this.peripheral.cast();
//		return super.getCapability(cap, side);
	}

	// Implement protected methods.

	public boolean isConnected() {
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

	// CC

	public long getEnergy() {
		return this.capability != null ? this.capability.getAmount() : -1;
	}

	public long getCapacity() {
		return this.capability != null ? this.capability.getCapacity() : -1;
	}

	public class InterfaceEnergyHandler implements EnergyStorage {

		private final EnergyStorage wrapped;

		public InterfaceEnergyHandler(EnergyStorage wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public long insert(long maxReceive, TransactionContext transaction) {
			if (!PortableEnergyInterfaceTileEntity.this.canTransfer()) return 0;
			maxReceive = Math.min(maxReceive, Config.PEI_MAX_INPUT.get());
			if (this.wrapped == null) return 0;
			long received = this.wrapped.insert(maxReceive, transaction);
			if (received != 0)
				transaction.addCloseCallback(new TransactionSuccessCallback(transaction, this::keepAlive));
			return received;
		}

		@Override
		public long extract(long maxExtract, TransactionContext transaction) {
			if (!PortableEnergyInterfaceTileEntity.this.canTransfer()) return 0;
			maxExtract = Math.min(maxExtract, Config.PEI_MAX_OUTPUT.get());
			if (this.wrapped == null) return 0;
			long extracted = this.wrapped.extract(maxExtract, transaction);

			if (extracted != 0)
				transaction.addCloseCallback(new TransactionSuccessCallback(transaction, this::keepAlive));
			return extracted;
		}

		@Override
		public long getAmount() {
			if (this.wrapped == null) return 0;
			return this.wrapped.getAmount();
		}

		@Override
		public long getCapacity() {
			if (this.wrapped == null) return 0;
			return this.wrapped.getCapacity();
		}

		@Override
		public boolean supportsExtraction() {
			return true;
		}

		@Override
		public boolean supportsInsertion() {
			return true;
		}

		public void keepAlive() {
			PortableEnergyInterfaceTileEntity.this.onContentTransferred();
		}
	}
}
