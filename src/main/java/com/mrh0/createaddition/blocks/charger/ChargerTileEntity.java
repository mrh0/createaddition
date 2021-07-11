package com.mrh0.createaddition.blocks.charger;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.item.ChargingChromaticCompound;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargerTileEntity extends BaseElectricTileEntity implements IComparatorOverride, IHaveGoggleInformation {

	private ItemStack itemStack = ItemStack.EMPTY;

	private static final int MAX_IN = Config.CHARGER_MAX_INPUT.get(), CHARGE_RATE = Config.CHARGER_CHARGE_RATE.get(),
			CAPACITY = Config.CHARGER_CAPACITY.get();

	public ChargerTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, 0);

		setLazyTickRate(20);
	}

	private void chargeItem(ItemStack stack) {
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(itemEnergy -> {
			if (!isChargingItem(itemEnergy))
				return;

			int energyRemoved = itemEnergy.receiveEnergy(Math.min(energy.getEnergyStored(), CHARGE_RATE), false);
			energy.internalConsumeEnergy(energyRemoved);
		});
	}

	public boolean isChargingItem(IEnergyStorage energy) {
		return energy.getEnergyStored() >= 0;
	}

	public float getItemCharge(IEnergyStorage energy) {
		if (energy == null)
			return 0f;
		return (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != Direction.UP;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	private int lastComparator = 0;

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide())
			return;

		if (hasChargedStack()) {
			chargeItem(itemStack);

			if (itemStack.getItem() == AllItems.CHROMATIC_COMPOUND.get()) {
				setChargedStack(new ItemStack(CAItems.CHARGING_CHROMATIC_COMPOUND.get(), 1));
				return;
			}

			if (itemStack.getItem() == CAItems.CHARGING_CHROMATIC_COMPOUND.get()) {
				int energyRemoved = ChargingChromaticCompound.charge(itemStack,
						Math.min(energy.getEnergyStored(), CHARGE_RATE));
				energy.internalConsumeEnergy(energyRemoved);

				if (ChargingChromaticCompound.getEnergy(itemStack) >= ChargingChromaticCompound.MAX_CHARGE) {
					setChargedStack(new ItemStack(CAItems.OVERCHARGED_ALLOY.get(), 1));
				}
				return;
			}
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();

		int comp = getComparetorOverride();
		if (comp != lastComparator)
			level.updateNeighborsAt(worldPosition, CABlocks.CHARGER.get());
		lastComparator = comp;

		if (hasChargedStack())
			causeBlockUpdate();
	}

	@Override
	public void fromTag(BlockState state, CompoundNBT nbt, boolean clientPacket) {
		super.fromTag(state, nbt, clientPacket);
		itemStack = ItemStack.of(nbt.getCompound("item"));
		if (itemStack == null)
			itemStack = ItemStack.EMPTY;
	}

	@Override
	public void write(CompoundNBT nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		nbt.put("item", itemStack.save(new CompoundNBT()));
	}

	public void setChargedStack(ItemStack itemStack) {
		if (itemStack == null)
			itemStack = ItemStack.EMPTY;
		this.itemStack = itemStack.copy();
		this.itemStack.setCount(1);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
		this.setChanged();
	}

	public ItemStack getChargedStack() {
		return itemStack;
	}

	public boolean hasChargedStack() {
		return itemStack != null && !this.itemStack.isEmpty();
	}

	@Override
	public int getComparetorOverride() {
		return (int) (getCharge() * 15f);
	}

	public float getCharge() {
		if (!hasChargedStack())
			return 0f;
		if (itemStack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return getItemCharge(itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null));
		if (itemStack.getItem() == CAItems.CHARGING_CHROMATIC_COMPOUND.get())
			return (float) ChargingChromaticCompound.getCharge(itemStack) * 10f;
		if (itemStack.getItem() == CAItems.OVERCHARGED_ALLOY.get())
			return 10f;
		return 0f;
	}

	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		tooltip.add(new StringTextComponent(spacing).append(
				new TranslationTextComponent("block.createaddition.charger.info").withStyle(TextFormatting.WHITE)));
		if (hasChargedStack()) {
			tooltip.add(new StringTextComponent(spacing).append(" ")
					.append(new StringTextComponent(Math.round(getCharge() * 100) + "% ")
							.withStyle(TextFormatting.AQUA))
					.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.charged")
							.withStyle(TextFormatting.GRAY)));
		} else {
			tooltip.add(new StringTextComponent(spacing).append(" ").append(
					new TranslationTextComponent("block.createaddition.charger.empty").withStyle(TextFormatting.GRAY)));
		}

		return true;
	}
}
