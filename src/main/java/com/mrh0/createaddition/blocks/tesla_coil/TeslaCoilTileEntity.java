package com.mrh0.createaddition.blocks.tesla_coil;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.item.ChargingChromaticCompound;
import com.mrh0.createaddition.item.Multimeter;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TeslaCoilTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {

	private static final int MAX_IN = Config.CHARGER_MAX_INPUT.get(), CHARGE_RATE = Config.CHARGER_CHARGE_RATE.get(),
			CAPACITY = Config.CHARGER_CAPACITY.get();
	
	protected ItemStack chargedStackCache;
	
	public TeslaCoilTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1000, 1000, 1000);
	}
	
	public BeltProcessingBehaviour processingBehaviour;

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		processingBehaviour =
			new BeltProcessingBehaviour(this).whenItemEnters((s, i) -> TeslaCoilBeltCallbacks.onItemReceived(s, i, this))
				.whileItemHeld((s, i) -> TeslaCoilBeltCallbacks.whenItemHeld(s, i, this));
		behaviours.add(processingBehaviour);
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != getBlockState().getValue(TeslaCoil.FACING);
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	protected boolean canStackReceiveCharge(ItemStack stack) {
		if(stack == null)
			return false;
		if(!stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return false;
		IEnergyStorage es = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if(es.receiveEnergy(1, true) != 1)
			return false;
		return true;
	}
	
	protected boolean chargeStack(ItemStack stack) {
		if(stack == null)
			return false;
		if(!stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return false;
		IEnergyStorage es = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		energy.extractEnergy(es.receiveEnergy(energy.extractEnergy(getConsumption(), true), false), false);
		if(es.receiveEnergy(1, true) != 1)
			return false;
		return true;
	}
	
	public boolean hasEnoughEnergy() {
		return energy.getEnergyStored() > CHARGE_RATE;
	}
	
	public int getConsumption() {
		return CHARGE_RATE;
	}
	
	/*@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent("block.createaddition.tesla_coil.info").withStyle(TextFormatting.WHITE)));
		
		
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.format(hasEnoughEnergy() ? getConsumption() : 0) + "fe/t ")).withStyle(TextFormatting.AQUA));
		return true;
	}*/
	
	protected float getItemCharge(IEnergyStorage energy) {
		if (energy == null)
			return 0f;
		return (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
	}
	
	public float getCharge(ItemStack itemStack) {
		if (chargedStackCache != null)
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
		float c = Math.round(getCharge(chargedStackCache) * 100);
		if(c >= 9000)
			return "OVER9000% ";
		return Math.round(getCharge(chargedStackCache) * 100) + "% ";
	}
	
	/*@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		tooltip.add(new StringTextComponent(spacing).append(
				new TranslationTextComponent("block.createaddition.charger.info").withStyle(TextFormatting.WHITE)));
		if (chargedStackCache != null) {
			tooltip.add(new StringTextComponent(spacing).append(" ")
					.append(new StringTextComponent(getChargeString()).withStyle(TextFormatting.AQUA))
					.append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.charged")
							.withStyle(TextFormatting.GRAY)));
		} else {
			tooltip.add(new StringTextComponent(spacing).append(" ").append(
					new TranslationTextComponent("block.createaddition.charger.empty").withStyle(TextFormatting.GRAY)));
		}

		return true;
	}*/
	
	protected ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		ItemStack stack = transported.stack;
		if(chargeStack(stack))
			return ProcessingResult.HOLD;
		return ProcessingResult.PASS;
	}
	
	/*@Override
	public void tick() {
		super.tick();
		
		DepotBehaviour depot = TileEntityBehaviour.get(level, getBlockPos().below(2), DepotBehaviour.TYPE);
		if(depot == null) {
			chargedStackCache = null;
			return;
		}
		chargedStackCache = depot.getHeldItemStack();
	}*/
}
