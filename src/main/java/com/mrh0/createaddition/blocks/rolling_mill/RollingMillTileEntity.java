package com.mrh0.createaddition.blocks.rolling_mill;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipeType;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.ViewOnlyWrappedStorageView;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RollingMillTileEntity extends KineticBlockEntity implements SidedStorageBlockEntity {

	public ItemStackHandler inputInv;
	public ItemStackHandler outputInv;
	public Storage<ItemVariant> storage;
	public int timer;
	private RollingRecipe lastRecipe;
	
	/*private static final int
		STRESS = Config.ROLLING_MILL_STRESS.get(), 
		DURATION = Config.ROLLING_MILL_PROCESSING_DURATION.get();*/

	public RollingMillTileEntity(BlockEntityType<? extends RollingMillTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inputInv = new ItemStackHandler(1);
		outputInv = new ItemStackHandler(9);
		storage = new RollingMillInventoryHandler();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.add(new DirectBeltInputBehaviour(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		for (int i = 0; i < outputInv.getSlotCount(); i++)
			if (outputInv.getStackInSlot(i)
					.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0) {
			timer -= getProcessingSpeed();

			assert level != null;
			if (level.isClientSide) {
				spawnParticles();
				return;
			}
			if (timer <= 0)
				process();
			return;
		}

		//Note: this code below is taken and adapted from the Create repo, specifically:
		//https://github.com/Creators-of-Create/Create/blob/a92855254c9a7b85ba28781e2e3ce7169549cbf7/src/main/java/com/simibubi/create/content/contraptions/components/saw/SawTileEntity.java#L190,
		var ejectDirection = getEjectDirection();
		for (int slot = 0; slot < outputInv.getSlotCount(); slot++) {
			var stack = outputInv.getStackInSlot(slot);
			if(stack.isEmpty())
				continue;
			ItemStack tryExport = getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(stack,ejectDirection,false);
			if(tryExport != null) {
				if(tryExport.getCount() != stack.getCount()) {
					outputInv.setStackInSlot(slot,tryExport);
					setChanged();
					sendData();
				}
			}
		}

		var step = new Vec3i(ejectDirection.getStepX(),ejectDirection.getStepY(),ejectDirection.getStepZ());
		BlockPos nextPos = getBlockPos().offset(step);
		DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(level,nextPos,DirectBeltInputBehaviour.TYPE);
		if(behaviour != null) {
			boolean changed = false;
			if(level.isClientSide && !isVirtual())
				return;
			for (int slot = 0; slot < outputInv.getSlotCount(); slot++) {
				var stack = outputInv.getStackInSlot(slot);
				if(stack.isEmpty())
					continue;
				ItemStack rest = behaviour.handleInsertion(stack,ejectDirection,false);
				if(!stack.isEmpty() && rest.getCount() == stack.getCount() && rest.getItem() == stack.getItem() && ItemStack.isSameItemSameTags(rest, stack))
					continue;
				outputInv.setStackInSlot(slot,rest);
				changed = true;
			}
			if(changed) {
				setChanged();
				sendData();
			}
		}
		//end of copied code

		if (inputInv.getStackInSlot(0)
				.isEmpty())
			return;

		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, Objects.requireNonNull(level))) {
			assert level != null;
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (recipe.isEmpty()) {
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

	private Direction getEjectDirection() {
		var block = ((RollingMillBlock) getBlockState().getBlock());
		var speed = getSpeed();
		block.getRotationAxis(getBlockState());
		boolean rotation = speed >= 0;
		Direction ejectDirection = Direction.UP;
		switch (block.getRotationAxis(getBlockState())) {
			case X -> {
				ejectDirection = rotation ? Direction.SOUTH : Direction.NORTH;
			}
			case Z -> {
				ejectDirection = rotation ? Direction.WEST : Direction.EAST;
			}
		}
		return ejectDirection;
	}
	
	private void process() {
		final var inventoryIn = new RecipeWrapper(inputInv);

		var sequenced = SequencedAssemblyRecipe.getRecipe(level,inventoryIn.getItem(0), RollingRecipe.TYPE,RollingRecipe.class);
		if(sequenced.isPresent()) {
			var recipe = sequenced.get();
			var results = recipe.rollResults();
			if(!results.isEmpty()) {
				var result = results.get(0);
				TransferUtil.insertItem(outputInv, result);
				ItemStack stackInSlot = inputInv.getStackInSlot(0);
				stackInSlot.shrink(1);
				inputInv.setStackInSlot(0, stackInSlot);
				sendData();
				setChanged();
				return;
			}
		}

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, Objects.requireNonNull(level))) {
			assert level != null;
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (recipe.isEmpty())
				return;
			lastRecipe = recipe.get();
		}

		ItemStack result = lastRecipe.assemble(inventoryIn, null).copy();
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
		assert level != null;
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
//	public <T> LazyOptional<T> getStorage(Capability<T> cap, Direction side) {
//		if (isItemHandlerCap(cap))
//			return capability.cast();
//		return super.getStorage(cap, side);
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

		var sequenced = SequencedAssemblyRecipe.getRecipe(level,stack,RollingRecipe.TYPE,RollingRecipe.class);
		if(sequenced.isPresent()) {
			return true;
		}

		if (lastRecipe != null) {
			assert level != null;
			if (lastRecipe.matches(inventoryIn, level)) return true;
		}
		assert level != null;
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
		public @NotNull Iterator<StorageView<ItemVariant>> iterator(){
			return new RollingMillInventoryHandlerIterator();
		}

		private class RollingMillInventoryHandlerIterator implements Iterator<StorageView<ItemVariant>> {
			private boolean output = true;
			private Iterator<StorageView<ItemVariant>> wrapped;

			public RollingMillInventoryHandlerIterator(){
				wrapped = outputInv.iterator();
			}

			@Override
			public boolean hasNext(){
				return wrapped.hasNext();
			}

			@Override
			public StorageView<ItemVariant> next(){
				StorageView<ItemVariant> view = wrapped.next();
				if(!output) view = new ViewOnlyWrappedStorageView<>(view);
				if(output && !hasNext()){
					wrapped = inputInv.iterator();
					output = false;
				}
				return view;
			}
		}
	}
	public Optional<RollingRecipe> find(RecipeWrapper inv, Level world) {
		var sequenced = SequencedAssemblyRecipe.getRecipe(level,inv.getItem(0), new RollingRecipeType(),RollingRecipe.class);
		if(sequenced.isPresent()) {
			return sequenced;
		}
		return world.getRecipeManager().getRecipeFor(RollingRecipe.TYPE, inv, world);
	}

	public static int getProcessingDuration() {
		return Config.ROLLING_MILL_PROCESSING_DURATION.get();
	}

	public float calculateStressApplied() {
		float impact = Config.ROLLING_MILL_STRESS.get();
		this.lastStressApplied = impact;
		return impact;
	}
}

