package com.mrh0.createaddition.blocks.tesla_coil;

import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAEffects;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.DamageSourceAccessor;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class TeslaCoilTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<ChargingRecipe> recipeCache;

	private final ItemStackHandler inputInv;
	private int chargeAccumulator;

	private static final long
		MAX_IN = Config.TESLA_COIL_MAX_INPUT.get(),
		CHARGE_RATE = Config.TESLA_COIL_CHARGE_RATE.get(),
		CHARGE_RATE_RECIPE = Config.TESLA_COIL_RECIPE_CHARGE_RATE.get(),
		CAPACITY = Math.max(Config.TESLA_COIL_CAPACITY.get(), CHARGE_RATE),
		HURT_ENERGY_REQUIRED = Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get(),
		HURT_DMG_MOB = Config.TESLA_COIL_HURT_DMG_MOB.get(),
		HURT_DMG_PLAYER = Config.TESLA_COIL_HURT_DMG_PLAYER.get(),
		HURT_RANGE = Config.TESLA_COIL_HURT_RANGE.get(),
		HURT_EFFECT_TIME_MOB = Config.TESLA_COIL_HURT_EFFECT_TIME_MOB.get(),
		HURT_EFFECT_TIME_PLAYER = Config.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get(),
		HURT_FIRE_COOLDOWN = Config.TESLA_COIL_HURT_FIRE_COOLDOWN.get();

	protected int poweredTimer = 0;
	
	private static final DamageSource dmgSource = DamageSourceAccessor.port_lib$init("tesla_coil");
	
	public TeslaCoilTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, 0);
		inputInv = new ItemStackHandler(1);
		recipeCache = Optional.empty();
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

	protected ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		return chargeCompundAndStack(transported, handler);
	}
	
	private void doDmg() {
		energy.internalConsumeEnergy(HURT_ENERGY_REQUIRED);
		BlockPos origin = getBlockPos().relative(getBlockState().getValue(TeslaCoil.FACING).getOpposite());
		List<LivingEntity> ents = Objects.requireNonNull(getLevel()).getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(HURT_RANGE));
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
		if (level != null && level.isClientSide()) return;
		int signal = Objects.requireNonNull(getLevel()).getBestNeighborSignal(getBlockPos());
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

	protected final boolean chargeStack(
			final ItemStack stack,
			final TransportedItemStack ignoredTransported,
			final TransportedItemStackHandlerBehaviour ignoredHandler
	) {
		final var energyTag = "energy";
		final EnergyStorage itemEnergy =  EnergyStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack));

		if (itemEnergy == null)
			return false;

		if (stack.getTag() == null)
			stack.setTag(new CompoundTag());

		// Make sure the targeted stack is energy storage
		if (EnergyStorageUtil.isEnergyStorage(stack)) {
			if (energy.getAmount() < stack.getCount())
				return false;

			try (Transaction t = TransferUtil.getTransaction()) {
				final var amountToUse = Math.min(getConsumption(), energy.getAmount());
				final var compoundTag = stack.getTag();
				final var energyAmount  = compoundTag.getDouble(energyTag);
				final var energyCapacity = itemEnergy.getCapacity();
				final var newAmount = energyAmount + amountToUse;

				if (newAmount < itemEnergy.getCapacity()) {
					energy.internalConsumeEnergy(itemEnergy.insert(amountToUse, t));
					compoundTag.put(energyTag, DoubleTag.valueOf(newAmount));
					t.commit();
				} else if (energyAmount < energyCapacity) {
					final long energyDiff = energyCapacity - (long) energyAmount;
					System.out.println(energyDiff);
					energy.internalConsumeEnergy(itemEnergy.insert(energyDiff, t));
					compoundTag.put(energyTag, DoubleTag.valueOf(energyAmount + energyDiff));
					t.commit();
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private boolean chargeRecipe(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(!inputInv.getStackInSlot(0).sameItem(stack)) {
			inputInv.setStackInSlot(0, stack);
			recipeCache = find(new RecipeWrapper(inputInv), Objects.requireNonNull(this.getLevel()));
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get();
			long energyRemoved = energy.internalConsumeEnergy(Math.min(CHARGE_RATE_RECIPE, recipe.getEnergy() - chargeAccumulator));
chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				TransportedItemStack left = transported.copy();
				left.stack.shrink(1);
				List<TransportedItemStack> r = new ArrayList<>();
				r.add(new TransportedItemStack(recipe.getResultItem().copy()));
				handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(r, left));
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
