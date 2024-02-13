package com.mrh0.createaddition.blocks.rolling_mill;

import com.google.common.collect.ImmutableList;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.VecHelper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RollingMillBlockEntity extends KineticBlockEntity implements SidedStorageBlockEntity {

	private static final Object rollingRecipesKey = new Object();
	public ProcessingInventory inventory;
	private int recipeIndex;
	private ItemStack playEvent;

	
	/*private static final int
		STRESS = Config.ROLLING_MILL_STRESS.get(), 
		DURATION = Config.ROLLING_MILL_PROCESSING_DURATION.get();*/

	public RollingMillBlockEntity(BlockEntityType<? extends RollingMillBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ProcessingInventory(this::start);
		inventory.remainingTime = -1;
		recipeIndex = 0;
		playEvent = ItemStack.EMPTY;
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
		if (inventory.remainingTime == -1) {
			if (!inventory.isEmpty() && !inventory.appliedRecipe)
				start(inventory.getStackInSlot(0));
			return;
		}

		float processingSpeed = Mth.clamp(Math.abs(getSpeed()) / 24, 1, 128);
		inventory.remainingTime -= processingSpeed;

		if (inventory.remainingTime > 0)
			spawnParticles();

		if (inventory.remainingTime < 5 && !inventory.appliedRecipe) {
			if (level.isClientSide && !isVirtual())
				return;
			playEvent = inventory.getStackInSlot(0);
			applyRecipe();
			inventory.appliedRecipe = true;
			inventory.recipeDuration = 20;
			inventory.remainingTime = 20;
			sendData();
			return;
		}

		Vec3 itemMovement = getItemMovementVec();
		Direction itemMovementFacing = getEjectDirection();
		if (inventory.remainingTime > 0)
			return;
		inventory.remainingTime = 0;

		for (int slot = 0; slot < inventory.getSlotCount(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty())
				continue;
			ItemStack tryExportingToBeltFunnel = getBehaviour(DirectBeltInputBehaviour.TYPE)
					.tryExportingToBeltFunnel(stack, itemMovementFacing.getOpposite(), false);
			if (tryExportingToBeltFunnel != null) {
				if (tryExportingToBeltFunnel.getCount() != stack.getCount()) {
					inventory.setStackInSlot(slot, tryExportingToBeltFunnel);
					notifyUpdate();
					return;
				}
				if (!tryExportingToBeltFunnel.isEmpty())
					return;
			}
		}

		BlockPos nextPos = worldPosition.relative(itemMovementFacing);
		DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(level, nextPos, DirectBeltInputBehaviour.TYPE);
		if (behaviour != null) {
			boolean changed = false;
			if (!behaviour.canInsertFromSide(itemMovementFacing))
				return;
			if (level.isClientSide && !isVirtual())
				return;
			for (int slot = 0; slot < inventory.getSlotCount(); slot++) {
				ItemStack stack = inventory.getStackInSlot(slot);
				if (stack.isEmpty())
					continue;
				ItemStack remainder = behaviour.handleInsertion(stack, itemMovementFacing, false);
				if (ItemStack.matches(remainder, stack))
					continue;
				inventory.setStackInSlot(slot, remainder);
				changed = true;
			}
			if (changed) {
				setChanged();
				sendData();
			}
			return;
		}

		// Eject Items
		Vec3 outPos = VecHelper.getCenterOf(worldPosition)
				.add(itemMovement.scale(.5f)
						.add(0, .5, 0));
		Vec3 outMotion = itemMovement.scale(.0625)
				.add(0, .125, 0);
		for (int slot = 0; slot < inventory.getSlotCount(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty())
				continue;
			ItemEntity entityIn = new ItemEntity(level, outPos.x, outPos.y, outPos.z, stack);
			entityIn.setDeltaMovement(outMotion);
			level.addFreshEntity(entityIn);
		}
		inventory.clear();
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		inventory.remainingTime = -1;
		sendData();
	}


	/*
	public void oldTick() {
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
		for (int slot = 0; slot < outputInv.getSlots(); slot++) {
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
			for (int slot = 0; slot < outputInv.getSlots(); slot++) {
				var stack = outputInv.getStackInSlot(slot);
				if(stack.isEmpty())
					continue;
				ItemStack rest = behaviour.handleInsertion(stack,ejectDirection,false);
				if(!stack.isEmpty() && rest.getCount() == stack.getCount() && rest.getItem() == stack.getItem() && ItemStack.tagMatches(rest, stack))
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

	 */

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inventory);
	}

	public void spawnParticles() {
		ItemStack stackInSlot = playEvent.copy();
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

	public Vec3 getItemMovementVec() {
		var dir = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		boolean alongX = dir == Direction.NORTH || dir == Direction.SOUTH;
		int offset = getSpeed() < 0 ? -1 : 1;
		return new Vec3(offset * (alongX ? -1 : 0), 0, offset * (alongX ? 0 : 1));
	}

	private void applyRecipe() {
		List<? extends Recipe<?>> recipes = getRecipes();
		if (recipes.isEmpty())
			return;
		if (recipeIndex >= recipes.size())
			recipeIndex = 0;

		Recipe<?> recipe = recipes.get(recipeIndex);

		int rolls = inventory.getStackInSlot(0)
				.getCount();
		inventory.clear();

		List<ItemStack> list = new ArrayList<>();
		for (int roll = 0; roll < rolls; roll++) {
			List<ItemStack> results = new LinkedList<>();
			if (recipe instanceof RollingRecipe)
				results = ((RollingRecipe) recipe).rollResults();

			for (int i = 0; i < results.size(); i++) {
				ItemStack stack = results.get(i);
				ItemHelper.addToList(stack, list);
			}
		}

		for (int slot = 0; slot < list.size() && slot + 1 < inventory.getSlotCount(); slot++)
			inventory.setStackInSlot(slot + 1, list.get(slot));

	}

	private List<? extends Recipe<?>> getRecipes() {
		Optional<RollingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inventory.getStackInSlot(0),
				CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
		if (assemblyRecipe.isPresent())
			return ImmutableList.of(assemblyRecipe.get());

		Predicate<Recipe<?>> types = RecipeConditions.isOfType(CARecipes.ROLLING_TYPE.get());

		List<Recipe<?>> startedSearch = RecipeFinder.get(rollingRecipesKey, level, types);
		return startedSearch.stream()
				.filter(RecipeConditions.firstIngredientMatches(inventory.getStackInSlot(0)))
				.filter(r -> !AllRecipeTypes.shouldIgnoreInAutomation(r))
				.collect(Collectors.toList());
	}

	public void insertItem(ItemEntity entity) {
		//if (!canProcess()) return;
		if (!inventory.isEmpty())
			return;
		if (!entity.isAlive())
			return;
		if (level.isClientSide)
			return;

		inventory.clear();
		try (Transaction t = TransferUtil.getTransaction()) {
			ItemStack contained = entity.getItem();
			long inserted = inventory.insert(ItemVariant.of(contained), contained.getCount(), t);
			if (contained.getCount() == inserted)
				entity.discard();
			else
				entity.setItem(ItemHandlerHelper.copyStackWithSize(contained, (int) (contained.getCount() - inserted)));
			t.commit();
		}
	}

	public void start(ItemStack inserted) {
		//if (!canProcess()) return;
		if (inventory.isEmpty())
			return;
		if (level.isClientSide && !isVirtual())
			return;

		List<? extends Recipe<?>> recipes = getRecipes();
		boolean valid = !recipes.isEmpty();
		int time = 50;

		if (recipes.isEmpty()) {
			inventory.remainingTime = inventory.recipeDuration = 10;
			inventory.appliedRecipe = false;
			sendData();
			return;
		}

		if (valid) {
			recipeIndex++;
			if (recipeIndex >= recipes.size())
				recipeIndex = 0;
		}

		Recipe<?> recipe = recipes.get(recipeIndex);
		if (recipe instanceof RollingRecipe) {
			time = ((RollingRecipe) recipe).getProcessingDuration();
		}

		inventory.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
		inventory.recipeDuration = inventory.remainingTime;
		inventory.appliedRecipe = false;
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

	/*
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
	 */

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		//compound.putInt("Timer", timer);
		//compound.put("InputInventory", inputInv.serializeNBT());
		//compound.put("OutputInventory", outputInv.serializeNBT());
		//super.write(compound, clientPacket);

		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("RecipeIndex", recipeIndex);
		super.write(compound, clientPacket);

		if (!clientPacket || playEvent.isEmpty())
			return;
		compound.put("PlayEvent", NBTSerializer.serializeNBT(playEvent));
		playEvent = ItemStack.EMPTY;
	}
	
	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		//timer = compound.getInt("Timer");
		//inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		//outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		//super.read(compound, clientPacket);

		super.read(compound, clientPacket);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		recipeIndex = compound.getInt("RecipeIndex");
		if (compound.contains("PlayEvent"))
			playEvent = ItemStack.of(compound.getCompound("PlayEvent"));
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
		return inventory;
	}

	private boolean canProcess(ItemStack stack) {
		ItemStackHandler tester = new ItemStackHandler(1);
		var stack2 = playEvent;
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		var sequenced = SequencedAssemblyRecipe.getRecipe(level, stack, CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
		if(sequenced.isPresent()) {
			return true;
		}

		assert level != null;
		return find(inventoryIn, level)
			.isPresent();
	}


	/*
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
	 */

	public Optional<RollingRecipe> find(RecipeWrapper inv, Level world) {
		var sequenced = SequencedAssemblyRecipe.getRecipe(level, inv.getItem(0), CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
		if(sequenced.isPresent()) {
			return sequenced;
		}
		return world.getRecipeManager().getRecipeFor(CARecipes.ROLLING_TYPE.get(), inv, world);
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

