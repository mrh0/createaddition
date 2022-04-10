package com.mrh0.createaddition.blocks.tesla_coil;

import java.util.ArrayList;
import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.compat.applied_energistics.AE2;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.item.ChargingChromaticCompound;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.DamageSourceAccessor;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

public class TeslaCoilTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {

	private static final long
			MAX_IN = Config.TESLA_COIL_MAX_INPUT.get(), 
			CHARGE_RATE = Config.TESLA_COIL_CHARGE_RATE.get(),
			CAPACITY = Math.max(Config.TESLA_COIL_CAPACITY.get(), CHARGE_RATE), 
			HURT_ENERGY_REQUIRED = Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get(), 
			HURT_DMG_MOB = Config.TESLA_COIL_HURT_DMG_MOB.get(),
			HURT_DMG_PLAYER = Config.TESLA_COIL_HURT_DMG_PLAYER.get(),
			HURT_RANGE = Config.TESLA_COIL_HURT_RANGE.get(), 
			HURT_EFFECT_TIME_MOB = Config.TESLA_COIL_HURT_EFFECT_TIME_MOB.get(),
			HURT_EFFECT_TIME_PLAYER = Config.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get(),
			HURT_FIRE_COOLDOWN = Config.TESLA_COIL_HURT_FIRE_COOLDOWN.get();
	private static final float CERTUS_QUARTZ_CHANCE = (float)(double)Config.CERTUS_QUARTZ_CHARGE_CHANCE.get();
	
	protected ItemStack chargedStackCache;
	protected int poweredTimer = 0;
	
	private static DamageSource dmgSource = DamageSourceAccessor.port_lib$init("tesla_coil");
	
	public TeslaCoilTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, 0);
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
		return side != getBlockState().getValue(TeslaCoil.FACING).getOpposite();
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	public long getConsumption() {
		return CHARGE_RATE;
	}
	
	protected float getItemCharge(EnergyStorage energy) {
		if (energy == null)
			return 0f;
		return (float) energy.getAmount() / (float) energy.getCapacity();
	}
	
	public float getCharge(ItemStack itemStack) {
		if (chargedStackCache != null)
			return 0f;
		if (EnergyStorage.ITEM.find(itemStack, ContainerItemContext.withInitial(itemStack)) != null)
			return getItemCharge(EnergyStorage.ITEM.find(itemStack, ContainerItemContext.withInitial(itemStack)));
		/*if (itemStack.getItem() == CAItems.CHARGING_CHROMATIC_COMPOUND.get())
			return (float) ChargingChromaticCompound.getCharge(itemStack) * 90f;
		if (itemStack.getItem() == CAItems.OVERCHARGED_ALLOY.get())
			return 90f;*/
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
		ProcessingResult res = chargeCompundAndStack(transported, handler);
		return res;
	}
	
	private void doDmg() {
		energy.internalConsumeEnergy(HURT_ENERGY_REQUIRED);
		BlockPos origin = getBlockPos().relative(getBlockState().getValue(TeslaCoil.FACING).getOpposite());
		List<LivingEntity> ents = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(HURT_RANGE));
		for(LivingEntity e : ents) {
			long dmg = HURT_DMG_MOB;
			long time = HURT_EFFECT_TIME_MOB;
			if(e instanceof Player) {
				dmg = HURT_DMG_PLAYER;
				time = HURT_EFFECT_TIME_PLAYER;
			}
			if(dmg > 0)
				e.hurt(dmgSource, dmg);
			if(time > 0)
				e.addEffect(new MobEffectInstance(CAEffects.SHOCKING, (int) time));
		}
	}
	
	int dmgTick = 0;
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide())
			return;
		int signal = getLevel().getBestNeighborSignal(getBlockPos());
		//System.out.println(signal + ":" + (energy.getEnergyStored() >= HURT_ENERGY_REQUIRED));
		if(signal > 0 && energy.getAmount() >= HURT_ENERGY_REQUIRED)
			poweredTimer = 10;
		
		dmgTick++;
		if((dmgTick%=HURT_FIRE_COOLDOWN) == 0 && energy.getAmount() >= HURT_ENERGY_REQUIRED && signal > 0)
			doDmg();
		
		if(poweredTimer > 0) {
			if(!isPoweredState())
				CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), true);
			poweredTimer--;
		}
		else
			if(isPoweredState())
				CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), false);
	}
	
	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoil.POWERED);
	}
	
	protected ProcessingResult chargeCompundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		
		ItemStack stack = transported.stack;
		if(stack == null)
			return ProcessingResult.PASS;
		/*if(stack.getItem() == AllItems.CHROMATIC_COMPOUND.get()) {
			TransportedItemStack res = new TransportedItemStack(new ItemStack(CAItems.CHARGING_CHROMATIC_COMPOUND.get(), stack.getCount()));
			handler.handleProcessingOnItem(transported, TransportedResult.convertTo(res));
		}*/
		if(chargeStack(stack, transported, handler)) {
			poweredTimer = 10;
			return ProcessingResult.HOLD;
		}
		else if(chargeAE2(stack, transported, handler)) {
			if(energy.getAmount() >= CHARGE_RATE)
				poweredTimer = 10;
			return ProcessingResult.HOLD;
		}
		/*if (stack.getItem() == CAItems.CHARGING_CHROMATIC_COMPOUND.get()) {
			if(energy.getEnergyStored() >= stack.getCount())
				poweredTimer = 10;
			
			int energyPush = Math.min(energy.getEnergyStored(), getConsumption())/stack.getCount();
			int energyRemoved = ChargingChromaticCompound.charge(stack, energyPush);
			energy.internalConsumeEnergy(energyRemoved*stack.getCount());

			if (ChargingChromaticCompound.getEnergy(stack) >= ChargingChromaticCompound.MAX_CHARGE) {
				TransportedItemStack res = new TransportedItemStack(new ItemStack(CAItems.OVERCHARGED_ALLOY.get(), stack.getCount()));
				handler.handleProcessingOnItem(transported, TransportedResult.convertTo(res));
			}
			return ProcessingResult.HOLD;
		}*/
		return ProcessingResult.PASS;
	}
	
	protected boolean chargeStack(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		EnergyStorage es = EnergyStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack));
		if(es != null)
			return false;
		try(Transaction t = Transaction.openOuter()) {
			if (es.insert(1, t) != 1)
				return false;
		}
		if(energy.getAmount() < stack.getCount())
			return false;
		try(Transaction t = Transaction.openOuter()) {
			long r = energy.internalConsumeEnergy(es.insert(Math.min(getConsumption(), energy.getAmount()), t));
			t.commit();
		}
		return true;
	}
	
	protected boolean chargeAE2(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(!CreateAddition.AE2_ACTIVE)
			return false;
		if(!AE2.isCertusQuartz(stack))
			return false;
		long energyRemoved = energy.internalConsumeEnergy(getConsumption());

		if(energyRemoved >= getConsumption() && level.random.nextFloat() > CERTUS_QUARTZ_CHANCE) {
			TransportedItemStack left = transported.copy();
			left.stack.shrink(1);
			List<TransportedItemStack> r = new ArrayList<>();
			r.add(new TransportedItemStack(AE2.getChargedCertusQuartz(1)));
			//.ifPresent(is -> r.add(new TransportedItemStack(is)));
			handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(r, left));
		}
		return true;
	}
}
