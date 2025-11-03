package com.mrh0.createaddition.blocks.rolling_mill;

import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.index.CARecipes;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.createmod.catnip.math.VecHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class RollingMillBlockEntity extends KineticBlockEntity implements SidedStorageBlockEntity {
	public ItemStackHandler inputInv;
	public ItemStackHandler outputInv;
	public Storage<ItemVariant> storage;
	public int timer;
	private RollingRecipe lastRecipe;

	public RollingMillBlockEntity(BlockEntityType<? extends RollingMillBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inputInv = new ItemStackHandler(1);
		outputInv = new ItemStackHandler(9);
		storage = new CombinedStorage<>(List.of(FilteringStorage.insertOnlyOf(inputInv), FilteringStorage.extractOnlyOf(outputInv)));
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
			var stack = outputInv.getStackInSlot(slot).copy();
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
				var stack = outputInv.getStackInSlot(slot).copy();
				if(stack.isEmpty())
					continue;
				ItemStack rest = behaviour.handleInsertion(stack, ejectDirection, false);
				if(rest.equals(stack))
					continue;
				outputInv.setStackInSlot(slot, rest);
				changed = true;
			}
			if(changed) {
				setChanged();
				sendData();
			}
		}
		//end of copied code

		if (inputInv.getStackInSlot(0).isEmpty()) return;

		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);
		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (recipe.isEmpty()) {
				timer = 100;
			} else {
				lastRecipe = recipe.get();
				timer = getProcessingDuration();
			}
			sendData();
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

	@Override
	public void remove() {
		super.remove();
	}

	private void process() {
		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);

		var sequenced = SequencedAssemblyRecipe.getRecipe(level, inventoryIn.getItem(0), CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
		if(sequenced.isPresent()) {
			var recipe = sequenced.get();
			var results = recipe.rollResults();
			if(!results.isEmpty()) {
				var result = results.get(0);

				try (Transaction t = Transaction.openOuter()) {
					long inserted = StorageUtil.insertStacking(outputInv.getSlots(), ItemVariant.of(result), result.getCount(), t);
					t.commit();
				}
				ItemStack stackInSlot = inputInv.getStackInSlot(0).copy();
				stackInSlot.shrink(1);
				inputInv.setStackInSlot(0, stackInSlot);
				sendData();
				setChanged();
				return;
			}
		}

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<RollingRecipe> recipe = find(inventoryIn, level);
			if (recipe.isEmpty()) return;
			lastRecipe = recipe.get();
		}

		ItemStack result = lastRecipe.assemble(inventoryIn, level.registryAccess()).copy();
		try(Transaction t = Transaction.openOuter()) {
			StorageUtil.insertStacking(outputInv.getSlots(), ItemVariant.of(result), result.getCount(), t);
			t.commit();
		}
		ItemStack stackInSlot = inputInv.getStackInSlot(0).copy();
		stackInSlot.shrink(1); //lastRecipe.getIngredient().getItems()[0].getCount()
		inputInv.setStackInSlot(0, stackInSlot);
		sendData();
		setChanged();
	}

	public void spawnParticles() {
		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		if (stackInSlot.isEmpty()) return;

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
		if(compound.contains("Inventory")) {
			ProcessingInventory inventory = new ProcessingInventory((stack) -> {});
			inventory.deserializeNBT(compound.getCompound("Inventory"));
			try(Transaction t = Transaction.openOuter()) {
				StorageUtil.move(inventory, inputInv, variant -> true, Long.MAX_VALUE, t);
			}
		}

		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));

		super.read(compound, clientPacket);
	}

	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	@Override
	public @Nullable Storage<ItemVariant> getItemStorage(Direction side) {
		return storage;
	}

	private boolean canProcess(ItemStack stack) {
		ItemStackHandler tester = new ItemStackHandler(1);
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		var sequenced = SequencedAssemblyRecipe.getRecipe(level,stack,CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
		if(sequenced.isPresent()) {
			return true;
		}

		if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
			return true;
		return find(inventoryIn, level)
				.isPresent();
	}

	public Optional<RollingRecipe> find(RecipeWrapper inv, Level world) {
		var sequenced = SequencedAssemblyRecipe.getRecipe(level,inv.getItem(0), CARecipes.ROLLING_TYPE.get(), RollingRecipe.class);
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


