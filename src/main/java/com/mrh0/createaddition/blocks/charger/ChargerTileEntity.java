package com.mrh0.createaddition.blocks.charger;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.item.ChargingChromaticCompound;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

public class ChargerTileEntity extends BaseElectricTileEntity implements IComparatorOverride, IHaveGoggleInformation {

	//private ItemStack itemStack = ItemStack.EMPTY;
	
	// Depot
	ChargerBehaviour behav;

	private static final int MAX_IN = Config.CHARGER_MAX_INPUT.get(), CHARGE_RATE = Config.CHARGER_CHARGE_RATE.get(),
			CAPACITY = Config.CHARGER_CAPACITY.get();

	public ChargerTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, 0);

		setLazyTickRate(20);
	}
	
	@Override
	public boolean isEnergyInput(Direction side) {
		return side != Direction.UP;
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	protected void chargeItem(ItemStack stack) {
		if(stack == null)
			return;
		stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(itemEnergy -> {
			if (!isChargingItem(itemEnergy))
				return;

			int energyRemoved = itemEnergy.receiveEnergy(Math.min(energy.getEnergyStored(), CHARGE_RATE), false);
			energy.internalConsumeEnergy(energyRemoved);
		});
	}
	
	protected boolean canReceiveCharge(ItemStack stack) {
		if(stack == null)
			return false;
		if(!stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return false;
		IEnergyStorage es = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if(es.receiveEnergy(1, true) != 1)
			return false;
		return true;
	}

	protected boolean isChargingItem(IEnergyStorage energy) {
		return energy.getEnergyStored() >= 0;
	}

	protected float getItemCharge(IEnergyStorage energy) {
		if (energy == null)
			return 0f;
		return (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
	}

	public ItemStack getChargedStack() {
		return behav.getHeldItemStack();
	}
	
	public boolean hasChargedStack() {
		return getChargedStack() != null && !this.getChargedStack().isEmpty();
	}
	

	/*private int lastComparator = 0;

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

	

	

	@Override
	public int getComparetorOverride() {
		return (int) (getCharge() * 15f);
	}

	

	*/
	
	public float getCharge(ItemStack itemStack) {
		if (!hasChargedStack())
			return 0f;
		if (itemStack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return getItemCharge(itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null));
		if (itemStack.getItem() == CAItems.CHARGING_CHROMATIC_COMPOUND.get())
			return (float) ChargingChromaticCompound.getCharge(itemStack) * 90f;
		if (itemStack.getItem() == CAItems.OVERCHARGED_ALLOY.get())
			return 90f;
		return 0f;
	}
	
	public String getChargeString() {
		float c = Math.round(getCharge(getChargedStack()) * 100);
		if(c >= 9000)
			return "OVER9000% ";
		return Math.round(getCharge(getChargedStack()) * 100) + "% ";
	}

	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		tooltip.add(new StringTextComponent(spacing).append(
				new TranslationTextComponent("block.createaddition.charger.info").withStyle(TextFormatting.WHITE)));
		if (hasChargedStack()) {
			tooltip.add(new StringTextComponent(spacing).append(" ")
					.append(new StringTextComponent(getChargeString()).withStyle(TextFormatting.AQUA))
					.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.charged")
							.withStyle(TextFormatting.GRAY)));
		} else {
			tooltip.add(new StringTextComponent(spacing).append(" ").append(
					new TranslationTextComponent("block.createaddition.charger.empty").withStyle(TextFormatting.GRAY)));
		}

		return true;
	}
	
	
	// Depot
	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		behaviours.add(behav = new ChargerBehaviour(this));
		behav.addSubBehaviours(behaviours);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return behav.getItemCapability(cap, side);
		return super.getCapability(cap, side);
	}

	@Override
	public int getComparetorOverride() {
		return 0;
	}
}
