package com.mrh0.createaddition.blocks.crude_burner;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.recipe.FluidRecipeWrapper;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class CrudeBurnerTileEntity extends AbstractBurnerBlockEntity implements IHaveGoggleInformation, IComparatorOverride, FluidTransferable {
	
	private static final int[] SLOTS = new int[] { };
	protected FluidTank tankInventory;
	public static final long capacity = FluidConstants.BUCKET;

	private Optional<CrudeBurningRecipe> recipeCache = Optional.empty();
	private Fluid lastFluid = null;
	private int updateTimeout = 10;
	private boolean changed = true;

	public CrudeBurnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		tankInventory = createInventory();
	}
	
	protected SmartFluidTank createInventory() {
		return new SmartFluidTank(capacity, this::onFluidStackChanged);
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
					TransferUtil.extractAnyFluid(be.tankInventory, FluidConstants.INGOT);
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
	public Storage<FluidVariant> getFluidStorage(@Nullable Direction face) {
		return tankInventory;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		if(nbt == null)
			nbt = new CompoundTag();
		super.read(nbt, clientPacket);
		tankInventory.readFromNBT(nbt.getCompound("TankContent"));
	}
	
	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		nbt.put("TankContent", tankInventory.writeToNBT(new CompoundTag()));
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		read(pkt.getTag(), true);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		//ObservePacket.send(worldPosition, 0);
		return containedFluidTooltip(tooltip, isPlayerSneaking, TransferUtil.getFluidStorage(this));
	}
	
	public Optional<CrudeBurningRecipe> find(FluidStack stack, Level world) {
		return world.getRecipeManager().getRecipeFor(CrudeBurningRecipe.TYPE, new FluidRecipeWrapper(stack), world);
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacket pack) {
		causeBlockUpdate();
	}

	@Override
	public int getComparetorOverride() {
		return (int)((double)tankInventory.getFluidAmount() / (double)capacity * 15d);
	}
}
