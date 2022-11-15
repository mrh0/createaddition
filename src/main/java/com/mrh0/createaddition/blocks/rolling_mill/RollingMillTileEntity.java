package com.mrh0.createaddition.blocks.rolling_mill;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.millstone.MillstoneTileEntity;
import com.simibubi.create.foundation.utility.VecHelper;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.ViewOnlyWrappedStorageView;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class RollingMillTileEntity extends KineticTileEntity implements ItemTransferable {

	public ItemStackHandler inputInv;
	public ItemStackHandler outputInv;
	public Storage<ItemVariant> storage;
	public int timer;
	private RollingRecipe lastRecipe;
	
	private static final int 
		STRESS = Config.ROLLING_MILL_STRESS.get(), 
		DURATION = Config.ROLLING_MILL_PROCESSING_DURATION.get();

	public RollingMillTileEntity(BlockEntityType<? extends RollingMillTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inputInv = new ItemStackHandler(1);
		outputInv = new ItemStackHandler(9);
		storage = new RollingMillInventoryHandler();
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		for (int i = 0; i < outputInv.getSlots(); i++)
			if (outputInv.getStackInSlot(i)
				.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0) {
			timer -= getProcessingSpeed();

			if (level.isClientSide) {
				spawnParticles();
				return;
			}
			if (timer <= 0)
				process();
			return;
		}

		if (inputInv.getStackInSlot(0)
			.isEmpty())
			return;

		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (!recipe.isPresent()) {
				timer = 100;
				sendData();
			} else {
				lastRecipe = recipe.get();
				timer = getProcessingDuration();
				sendData();
			}
			return;
		}

		timer = getProcessingDuration();
		sendData();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
//		capability.invalidate();
	}
	
	private void process() {
		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (!recipe.isPresent())
				return;
			lastRecipe = recipe.get();
		}

		ItemStack result = lastRecipe.assemble(inventoryIn).copy();
		try(Transaction t = TransferUtil.getTransaction()) {
			outputInv.insert(ItemVariant.of(result), result.getCount(), t);
			t.commit();
		}
		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		stackInSlot.shrink(1);
		inputInv.setStackInSlot(0, stackInSlot);
		sendData();
		setChanged();
	}

	public void spawnParticles() {
		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		if (stackInSlot.isEmpty())
			return;

		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.5f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT());
		compound.put("OutputInventory", outputInv.serializeNBT());
		super.write(compound, clientPacket);
	}
	
	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		super.read(compound, clientPacket);
	}
	
	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (isItemHandlerCap(cap))
//			return capability.cast();
//		return super.getCapability(cap, side);
//	}


	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		return storage;
	}

	private boolean canProcess(ItemStack stack) {
		ItemStackHandler tester = new ItemStackHandler(1);
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
			return true;
		return find(inventoryIn, level)
			.isPresent();
	}

	private class RollingMillInventoryHandler extends CombinedStorage<ItemVariant, ItemStackHandler> {

		public RollingMillInventoryHandler() {
			super(List.of(inputInv, outputInv));
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (canProcess(resource.toStack()))
				return inputInv.insert(resource, maxAmount, transaction);
			return 0;
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return outputInv.extract(resource, maxAmount, transaction);
		}

		@Override
		public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
			return new RollingMillInventoryHandlerIterator(transaction);
		}

		private class RollingMillInventoryHandlerIterator implements Iterator<StorageView<ItemVariant>> {
			private final TransactionContext ctx;
			private boolean open = true;
			private boolean output = true;
			private Iterator<StorageView<ItemVariant>> wrapped;

			public RollingMillInventoryHandlerIterator(TransactionContext ctx) {
				this.ctx = ctx;
				ctx.addCloseCallback((t, r) -> open = false);
				wrapped = outputInv.iterator(ctx);
			}

			@Override
			public boolean hasNext() {
				return open && wrapped.hasNext();
			}

			@Override
			public StorageView<ItemVariant> next() {
				StorageView<ItemVariant> view = wrapped.next();
				if (!output) view = new ViewOnlyWrappedStorageView<>(view);
				if (output && !hasNext()) {
					wrapped = inputInv.iterator(ctx);
					output = false;
				}
				return view;
			}
		}
	}

	public Optional<RollingRecipe> find(RecipeWrapper inv, Level world) {
		return world.getRecipeManager().getRecipeFor(RollingRecipe.TYPE, inv, world);
	}
	
	public static int getProcessingDuration() {
		return DURATION;
	}
	
	public float calculateStressApplied() {
		float impact = STRESS;
		this.lastStressApplied = impact;
		return impact;
	}
}

