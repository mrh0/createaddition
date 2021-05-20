package com.mrh0.createaddition.blocks.crude_burner;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CrudeBurnerTileEntity extends AbstractFurnaceTileEntity implements IHaveGoggleInformation {

	private static final int[] SLOTS = new int[] { };
	protected LazyOptional<IFluidHandler> fluidCapability;
	protected FluidTank tankInventory;
	
	private Optional<CrudeBurningRecipe> recipeCache = Optional.empty();
	private Fluid lastFluid = null;
	private int updateTimeout = 10;
	private boolean changed = true;

	public CrudeBurnerTileEntity(TileEntityType<?> type) {
		super(type, IRecipeType.SMELTING);
		tankInventory = createInventory();
		fluidCapability = LazyOptional.of(() -> tankInventory);
	}
	
	protected SmartFluidTank createInventory() {
		return new SmartFluidTank(4000, this::onFluidStackChanged);
	}

	protected void onFluidStackChanged(FluidStack newFluidStack) {
		if (!hasWorld())
			return;
		update(newFluidStack);
	}
	
	private void update(FluidStack stack) {
		if(world.isRemote())
			return;
		if(stack.getFluid() != lastFluid)
			recipeCache = find(stack, world);
		lastFluid = stack.getFluid();
		changed = true;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("");
	}

	@Override
	protected Container createMenu(int window, PlayerInventory inv) {
		return null;
	}

	@Override
	protected boolean canSmelt(IRecipe<?> recipe) {
		return true;
	}

	private boolean burning() {
		return this.burnTime > 0;
	}
	
	public int getBurnTime(FluidStack fluid) {
		return recipeCache.isPresent() ? recipeCache.get().getBurnTime() / 10 : 0;
	}
	
	boolean first = true;

	public void tick() {
		if(world.isRemote())
			return;
		if(first)
			update(tankInventory.getFluid());
		first = false;
		updateTimeout--;
		if(updateTimeout < 0)
			updateTimeout = 0;
		if(updateTimeout == 0 && changed) {
			if (!world.isRemote) {
				markDirty();
				if (world != null)
					world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2 | 4 | 16);
				changed = false;
			}
			updateTimeout = 10;
		}
		boolean flag = this.burning();
		boolean flag1 = false;
		if (this.burning())
			--this.burnTime;

		if (!this.world.isRemote()) {
			if (!this.burning()) {
				
				this.burnTime = getBurnTime(tankInventory.getFluid());
				if (this.burning()) {
					flag1 = true;
					updateTimeout = 0;
					tankInventory.drain(100, FluidAction.EXECUTE);
				}
			}

			if (flag != this.burning()) {
				flag1 = true;
				this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT,
						Boolean.valueOf(this.burning())), 3);
			}

			if (flag1)
				this.markDirty();
		}
	}

	public int[] getSlotsForFace(Direction dir) {
		return SLOTS;
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	public boolean canExtractItem(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fluidCapability.cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public void fromTag(BlockState state, CompoundNBT nbt) {
		super.fromTag(state, nbt);
		tankInventory.readFromNBT(nbt.getCompound("TankContent"));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.put("TankContent", tankInventory.writeToNBT(new CompoundNBT()));
		return super.write(nbt);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 1, write(new CompoundNBT()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		fromTag(getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		return containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));
	}
	
	public Optional<CrudeBurningRecipe> find(FluidStack stack, World world) {
		return world.getRecipeManager().getRecipe(CrudeBurningRecipe.TYPE, new FluidRecipeWrapper(stack), world);
	}
}
