package com.mrh0.createaddition.blocks.crude_burner;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;
import com.mrh0.createaddition.network.EnergyNetworkPacket;
import com.mrh0.createaddition.network.IObserveTileEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CrudeBurnerTileEntity extends AbstractBurnerBlockEntity implements IHaveGoggleInformation {
	
	private static final int[] SLOTS = new int[] { };
	protected LazyOptional<IFluidHandler> fluidCapability;
	protected FluidTank tankInventory;
	
	private Optional<CrudeBurningRecipe> recipeCache = Optional.empty();
	private Fluid lastFluid = null;
	private int updateTimeout = 10;
	private boolean changed = true;

	public CrudeBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		tankInventory = createInventory();
		fluidCapability = LazyOptional.of(() -> tankInventory);
	}
	
	protected SmartFluidTank createInventory() {
		return new SmartFluidTank(4000, this::onFluidStackChanged);
	}

	protected void onFluidStackChanged(FluidStack newFluidStack) {
		if (!hasLevel())
			return;
		update(newFluidStack);
	}
	
	private void update(FluidStack stack) {
		if(level.isClientSide())
			return;
		if(stack.getFluid() != lastFluid)
			recipeCache = find(stack, level);
		lastFluid = stack.getFluid();
		changed = true;
	}

	private boolean burning() {
		return this.litTime > 0; //this.litTime > 0;
	}
	
	public int getBurnTime(FluidStack fluid) {
		return recipeCache.isPresent() ? recipeCache.get().getBurnTime() / 10 : 0;
	}
	
	public boolean first = true;

	public static void crudeServerTick(Level level, BlockPos pos, BlockState state,
			CrudeBurnerTileEntity be) {
		if(level.isClientSide())
			return;
		if(be.first)
			be.update(be.tankInventory.getFluid());
		be.first = false;
		be.updateTimeout--;
		if(be.updateTimeout < 0)
			be.updateTimeout = 0;
		if(be.updateTimeout == 0 && be.changed) {
			if (!level.isClientSide) {
				be.setChanged();
				if (level != null)
					level.sendBlockUpdated(pos, state, state, 2 | 4 | 16);
				be.changed = false;
			}
			be.updateTimeout = 10;
		}
		boolean flag = be.burning();
		boolean flag1 = false;
		if (be.burning())
			--be.litTime;

		if (!be.level.isClientSide()) {
			if (!be.burning()) {
				
				be.litTime = be.getBurnTime(be.tankInventory.getFluid());
				if (be.burning()) {
					flag1 = true;
					be.updateTimeout = 0;
					be.tankInventory.drain(100, FluidAction.EXECUTE);
				}
			}

			if (flag != be.burning()) {
				flag1 = true;
				level.setBlock(pos, be.level.getBlockState(pos).setValue(AbstractFurnaceBlock.LIT,
						Boolean.valueOf(be.burning())), 3);
			}

			if (flag1)
				be.setChanged();
		}
	}

	public int[] getSlotsForFace(Direction dir) {
		return SLOTS;
	}

	public boolean canPlaceItem(int slot, ItemStack stack) {
		return false;
	}

	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fluidCapability.cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		if(nbt == null)
			nbt = new CompoundTag();
		super.load(nbt);
		tankInventory.readFromNBT(nbt.getCompound("TankContent"));
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		nbt.put("TankContent", tankInventory.writeToNBT(new CompoundTag()));
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		load(pkt.getTag());
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		//ObservePacket.send(worldPosition, 0);
		return containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));
	}
	
	public Optional<CrudeBurningRecipe> find(FluidStack stack, Level world) {
		return world.getRecipeManager().getRecipeFor(CrudeBurningRecipe.TYPE, new FluidRecipeWrapper(stack), world);
	}

	/*public void causeBlockUpdate() {
		if (level != null)
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
	}
	
	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		causeBlockUpdate();
	}*/
}
