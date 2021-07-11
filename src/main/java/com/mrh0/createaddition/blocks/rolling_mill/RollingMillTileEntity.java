package com.mrh0.createaddition.blocks.rolling_mill;

import java.util.Optional;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class RollingMillTileEntity extends KineticTileEntity {

	public ItemStackHandler inputInv;
	public ItemStackHandler outputInv;
	public LazyOptional<IItemHandler> capability;
	public int timer;
	private RollingRecipe lastRecipe;
	
	private static final int 
		STRESS = Config.ROLLING_MILL_STRESS.get(), 
		DURATION = Config.ROLLING_MILL_PROCESSING_DURATION.get();

	public RollingMillTileEntity(TileEntityType<? extends RollingMillTileEntity> type) {
		super(type);
		inputInv = new ItemStackHandler(1);
		outputInv = new ItemStackHandler(9);
		capability = LazyOptional.of(RollingMillInventoryHandler::new);
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
		capability.invalidate();
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
		ItemHandlerHelper.insertItemStacked(outputInv, result, false);
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

		ItemParticleData data = new ItemParticleData(ParticleTypes.ITEM, stackInSlot);
		float angle = level.random.nextFloat() * 360;
		Vector3d offset = new Vector3d(0, 0, 0.5f);
		offset = VecHelper.rotate(offset, angle, Axis.Y);
		Vector3d target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

		Vector3d center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}

	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT());
		compound.put("OutputInventory", outputInv.serializeNBT());
		super.write(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		super.fromTag(state, compound, clientPacket);
	}

	public int getProcessingSpeed() {
		return MathHelper.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (isItemHandlerCap(cap))
			return capability.cast();
		return super.getCapability(cap, side);
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

	private class RollingMillInventoryHandler extends CombinedInvWrapper {

		public RollingMillInventoryHandler() {
			super(inputInv, outputInv);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return canProcess(stack) && super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return stack;
			if (!isItemValid(slot, stack))
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

	}

	public Optional<RollingRecipe> find(RecipeWrapper inv, World world) {
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

	@Override
	public World getWorld() {
		return getLevel();
	}
}

