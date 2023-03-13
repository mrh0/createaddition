package com.mrh0.createaddition.blocks.tesla_coil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class TeslaCoilTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {

	private Optional<ChargingRecipe> recipeCache = Optional.empty();

	private ItemStackHandler inputInv;
	private int chargeAccumulator;

	private static final int
		MAX_IN = Config.TESLA_COIL_MAX_INPUT.get(),
		CHARGE_RATE = Config.TESLA_COIL_CHARGE_RATE.get(),
		CHARGE_RATE_RECIPE = Config.TESLA_COIL_RECIPE_CHARGE_RATE.get(),
		CAPACITY = Util.max(Config.TESLA_COIL_CAPACITY.get(), CHARGE_RATE, CHARGE_RATE_RECIPE),
		HURT_ENERGY_REQUIRED = Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get(),
		HURT_DMG_MOB = Config.TESLA_COIL_HURT_DMG_MOB.get(),
		HURT_DMG_PLAYER = Config.TESLA_COIL_HURT_DMG_PLAYER.get(),
		HURT_RANGE = Config.TESLA_COIL_HURT_RANGE.get(),
		HURT_EFFECT_TIME_MOB = Config.TESLA_COIL_HURT_EFFECT_TIME_MOB.get(),
		HURT_EFFECT_TIME_PLAYER = Config.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get(),
		HURT_FIRE_COOLDOWN = Config.TESLA_COIL_HURT_FIRE_COOLDOWN.get();

	protected ItemStack chargedStackCache;
	protected int poweredTimer = 0;

	private static DamageSource DMG_SOURCE = new DamageSource("tesla_coil");

	public TeslaCoilTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, 0);
		inputInv = new ItemStackHandler(1);
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
		return side != getBlockState().getValue(TeslaCoilBlock.FACING).getOpposite();
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	public int getConsumption() {
		return CHARGE_RATE;
	}

	protected float getItemCharge(IEnergyStorage energy) {
		if (energy == null)
			return 0f;
		return (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
	}

	protected ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		ProcessingResult res = chargeCompundAndStack(transported, handler);
		return res;
	}

	private void doDmg() {
		localEnergy.internalConsumeEnergy(HURT_ENERGY_REQUIRED);
		BlockPos origin = getBlockPos().relative(getBlockState().getValue(TeslaCoilBlock.FACING).getOpposite());
		List<LivingEntity> ents = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(HURT_RANGE));
		for(LivingEntity e : ents) {
			if(e == null) return;
			int dmg = HURT_DMG_MOB;
			int time = HURT_EFFECT_TIME_MOB;
			if(e instanceof Player) {
				dmg = HURT_DMG_PLAYER;
				time = HURT_EFFECT_TIME_PLAYER;
			}
			if(dmg > 0)
				e.hurt(DMG_SOURCE, dmg);
			if(time > 0)
				e.addEffect(new MobEffectInstance(CAEffects.SHOCKING.get(), time));
		}
	}

	int dmgTick = 0;
	int soundTimeout = 0;

	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide()) {
			if(isPoweredState() && soundTimeout++ > 20) {
				//level.playLocalSound(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 1f, 16f, false);
				soundTimeout = 0;
			}
			return;
		}
		int signal = getLevel().getBestNeighborSignal(getBlockPos());
		//System.out.println(signal + ":" + (energy.getEnergyStored() >= HURT_ENERGY_REQUIRED));
		if(signal > 0 && localEnergy.getEnergyStored() >= HURT_ENERGY_REQUIRED)
			poweredTimer = 10;

		dmgTick++;
		if((dmgTick%=HURT_FIRE_COOLDOWN) == 0 && localEnergy.getEnergyStored() >= HURT_ENERGY_REQUIRED && signal > 0)
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
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}

	protected ProcessingResult chargeCompundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {

		ItemStack stack = transported.stack;
		if(stack == null)
			return ProcessingResult.PASS;
		if(chargeStack(stack, transported, handler)) {
			poweredTimer = 10;
			return ProcessingResult.HOLD;
		}
		else if(chargeRecipe(stack, transported, handler)) {
			poweredTimer = 10;
			return ProcessingResult.HOLD;
		}
		return ProcessingResult.PASS;
	}

	protected boolean chargeStack(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(!stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
			return false;
		IEnergyStorage es = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
		if(es.receiveEnergy(1, true) != 1)
			return false;
		if(localEnergy.getEnergyStored() < stack.getCount())
			return false;
		localEnergy.internalConsumeEnergy(es.receiveEnergy(Math.min(getConsumption(), localEnergy.getEnergyStored()), false));
		return true;
	}

	private boolean chargeRecipe(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(!inputInv.getStackInSlot(0).sameItem(stack)) {
			inputInv.setStackInSlot(0, stack);
			recipeCache = find(new RecipeWrapper(inputInv), this.getLevel());
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get();
			int energyRemoved = localEnergy.internalConsumeEnergy(Math.min(CHARGE_RATE_RECIPE, recipe.getEnergy() - chargeAccumulator));
			chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				TransportedItemStack remainingStack = transported.copy();
				TransportedItemStack result = transported.copy();
				result.stack = recipe.getResultItem().copy();
				remainingStack.stack.shrink(1);
				List<TransportedItemStack> outList = new ArrayList<>();
				outList.add(result);
				handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(outList, remainingStack));
				chargeAccumulator = 0;
			}
			return true;
		}
		return false;
	}

	public Optional<ChargingRecipe> find(RecipeWrapper wrapper, Level world) {
		return world.getRecipeManager().getRecipeFor(ChargingRecipe.TYPE, wrapper, world);
	}
}
