package com.mrh0.createaddition.blocks.tesla_coil;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricBlockEntity;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.ObservePacket;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeslaCoilBlockEntity extends BaseElectricBlockEntity implements IHaveGoggleInformation {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<ChargingRecipe> recipeCache;

	private final ItemStackHandler inputInv;
	private int chargeAccumulator;
	protected int poweredTimer = 0;

	public TeslaCoilBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		inputInv = new ItemStackHandler(1);
		recipeCache = Optional.empty();
	}

	@Override
	public long getCapacity() {
		return Util.max(Config.TESLA_COIL_CAPACITY.get(), Config.TESLA_COIL_CHARGE_RATE.get(), Config.TESLA_COIL_RECIPE_CHARGE_RATE.get());
	}

	@Override
	public long getMaxIn() {
		return Config.TESLA_COIL_MAX_INPUT.get();
	}

	@Override
	public long getMaxOut() {
		return 0;
	}

	public BeltProcessingBehaviour processingBehaviour;

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
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

	public long getConsumption() {
		return Config.TESLA_COIL_CHARGE_RATE.get();
	}

	protected float getItemCharge(EnergyStorage energy) {
		if (energy == null) return 0f;
		return (float) energy.getAmount() / (float) energy.getCapacity();
	}

	protected BeltProcessingBehaviour.ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		BeltProcessingBehaviour.ProcessingResult res = chargeCompundAndStack(transported, handler);
		return res;
	}

	private void doDmg() {
		localEnergy.internalConsumeEnergy(Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get());
		BlockPos origin = getBlockPos().relative(getBlockState().getValue(TeslaCoilBlock.FACING).getOpposite());
		List<LivingEntity> ents = Objects.requireNonNull(getLevel()).getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(Config.TESLA_COIL_HURT_RANGE.get()));
		boolean zapped = false;
		for(LivingEntity e : ents) {
			if(e == null) return;

			boolean allChain = true;
			for(ItemStack armor : e.getArmorSlots()) {
				if(armor.is(Items.CHAINMAIL_BOOTS)) continue;
				if(armor.is(Items.CHAINMAIL_LEGGINGS)) continue;
				if(armor.is(Items.CHAINMAIL_CHESTPLATE)) continue;
				if(armor.is(Items.CHAINMAIL_HELMET)) continue;
				allChain = false;
				break;
			}
			if(allChain) continue;

			int dmg = Config.TESLA_COIL_HURT_DMG_MOB.get();
			int time = Config.TESLA_COIL_HURT_EFFECT_TIME_MOB.get();
			if(e instanceof Player) {
				dmg = Config.TESLA_COIL_HURT_DMG_PLAYER.get();
				time = Config.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get();
			}
			if(dmg > 0) {
				e.hurt(CADamageSources.teslaCoil(level), dmg);
				if (!zapped) {
					level.playSound(null, worldPosition, CASounds.LOUD_ZAP.get(), SoundSource.BLOCKS, 0.6f, 1f);
					zapped = true;
				}
			}
			if(time > 0) e.addEffect(new MobEffectInstance(CAEffects.SHOCKING.get(), time));
		}
	}

	int dmgTick = 0;
	int zapTimer = 200;

	@Override
	public void tick() {
		super.tick();
		if(level == null) return;

		if(level.isClientSide()) {
			tickAudio();
			return;
		}
		int signal = Objects.requireNonNull(getLevel()).getBestNeighborSignal(getBlockPos());
		if(signal > 0 && localEnergy.getAmount() >= Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get())
			poweredTimer = 10;

		dmgTick++;
		if((dmgTick%=Config.TESLA_COIL_HURT_FIRE_COOLDOWN.get()) == 0 && localEnergy.getAmount() >= Config.TESLA_COIL_HURT_ENERGY_REQUIRED.get() && signal > 0)
			doDmg();

		if(poweredTimer > 0) {
			if (zapTimer == 0) {
				level.playSound(null, worldPosition, CASounds.LITTLE_ZAP.get(), SoundSource.BLOCKS, 0.1f, 1f);
				zapTimer = level.random.nextInt(100, 300);
			}
			zapTimer--;

			if(!isPoweredState())
				CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), true);
			poweredTimer--;
		}
		else
			if(isPoweredState())
				CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), false);
	}

	public void tickAudio() {
		if (!isPoweredState()) return;
		CASoundScapes.play(CASoundScapes.AmbienceGroup.TESLA, worldPosition, 1f);
	}

	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}

	protected BeltProcessingBehaviour.ProcessingResult chargeCompundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {

		ItemStack stack = transported.stack;
		if(stack == null)
			return BeltProcessingBehaviour.ProcessingResult.PASS;
		if(chargeStack(stack, transported, handler)) {
			poweredTimer = 10;
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		else if(chargeRecipe(stack, transported, handler)) {
			poweredTimer = 10;
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		return BeltProcessingBehaviour.ProcessingResult.PASS;
	}

	protected final boolean chargeStack(
			final ItemStack stack,
			final TransportedItemStack ignoredTransported,
			final TransportedItemStackHandlerBehaviour ignoredHandler
	) {
		ContainerItemContext context = ContainerItemContext.withConstant(stack);
		final EnergyStorage es =  EnergyStorage.ITEM.find(stack, context);

		if (es == null)
			return false;
		try (Transaction t = TransferUtil.getTransaction()) {
			if (es.insert(1, t) != 1)
				return false;
		}
		if(localEnergy.getAmount() < stack.getCount())
			return false;
		try (Transaction t = TransferUtil.getTransaction()) {
			localEnergy.internalConsumeEnergy(es.insert(Math.min(getConsumption(), localEnergy.getAmount()), t));
			t.commit();
		}
		stack.setTag(context.getItemVariant().copyNbt());
		return true;
	}

	private boolean chargeRecipe(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if (this.getLevel() == null) return false;
		final var inventoryIn = new RecipeWrapper(inputInv);
		if(!inputInv.getStackInSlot(0).is(stack.getItem())) {
			inputInv.setStackInSlot(0, stack);
			recipeCache = find(stack, inventoryIn, level);
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get();
			long energyRemoved = localEnergy.internalConsumeEnergy(Util.min(Config.TESLA_COIL_RECIPE_CHARGE_RATE.get(), recipe.getEnergy() - chargeAccumulator, recipe.getMaxChargeRate()));
			chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				TransportedItemStack remainingStack = transported.copy();
				TransportedItemStack result = transported.copy();
				result.stack = recipe.getResultItem(null).copy();
				remainingStack.stack.shrink(1);
				List<TransportedItemStack> outList = new ArrayList<>();
				outList.add(result);
				handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, remainingStack));
				chargeAccumulator = 0;
				level.playSound(null, worldPosition, CASounds.LITTLE_ZAP.get(), SoundSource.BLOCKS, 0.1f, 1f);
			}
			return true;
		}
		return false;
	}

	public Optional<ChargingRecipe> find(ItemStack item, RecipeWrapper wrapper, Level level) {
		Optional<ChargingRecipe> assemblyRecipe =
				SequencedAssemblyRecipe.getRecipe(level, item, ChargingRecipe.TYPE, ChargingRecipe.class);
		if (assemblyRecipe.isPresent())
			return assemblyRecipe;
		return level.getRecipeManager().getRecipeFor(ChargingRecipe.TYPE, wrapper, level);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		ObservePacket.send(worldPosition, 0);

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.tesla_coil.info").withStyle(ChatFormatting.WHITE)));

		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.stored").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(Component.literal(" "))
				.append(Util.format(localEnergy.getAmount())).append("fe / ").append(Util.format(Config.TESLA_COIL_CAPACITY.get())).append("fe").withStyle(ChatFormatting.AQUA));

		/*
		tooltip.add(Component.literal(spacing)
				.append(Component.translatable(CreateAddition.MODID + ".tooltip.energy.usage").withStyle(ChatFormatting.GRAY)));
		tooltip.add(Component.literal(spacing).append(" ")
				.append(Util.format(EnergyNetworkPacket.clientBuff)).append("fe/t").withStyle(ChatFormatting.AQUA));

		 */

		return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
	}
}
