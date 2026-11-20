package com.mrh0.createaddition.blocks.generator_terminal;

import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nullable;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.rotor.IRotorAssemblyController;
import com.mrh0.createaddition.blocks.rotor.RotorAssembly;
import com.mrh0.createaddition.blocks.rotor.RotorAssemblyControllerBlock;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.IEnergyProvider;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.sound.CASoundScapes.AmbienceGroup;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class GeneratorTerminalBlockEntity extends KineticBlockEntity implements IEnergyProvider, IRotorAssemblyController {

	protected ScrollValueBehaviour excitation;
	protected final InternalEnergyStorage energy;
	private final RotorAssembly assembly = new RotorAssembly(this);
	private final EnumMap<Direction, BlockCapabilityCache<IEnergyStorage, Direction>> cache = new EnumMap<>(Direction.class);
	private boolean firstTick = true;

	public GeneratorTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(CommonConfig.GENERATOR_TERMINAL_CAPACITY.get(), 0, CommonConfig.GENERATOR_TERMINAL_MAX_OUTPUT.get());
		setLazyTickRate(10);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				CABlockEntities.GENERATOR_TERMINAL.get(),
				(be, side) -> RotorAssemblyControllerBlock.isEnergySide(be.getBlockState(), side) ? be.energy : null
		);
	}

	@Override
	public RotorAssembly getAssembly() {
		return assembly;
	}

	@Override
	public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
		return energy;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform(
				(state, side) -> side.getAxis() != state.getValue(RotorAssemblyControllerBlock.AXIS));
		excitation = new ScrollValueBehaviour(Component.translatable(CreateAddition.MODID + ".generic.excitation"), this, slot);
		excitation.between(0, 100);
		excitation.value = 100;
		excitation.withFormatter(v -> v + "%");
		excitation.withCallback(i -> updateStress());
		behaviours.add(excitation);
	}

	private float getExcitation() {
		return excitation == null ? 1f : excitation.getValue() / 100f;
	}

	private float getEffectiveStressAt256() {
		// assembly is null while SmartBlockEntity's constructor is still running.
		return assembly == null ? 0 : assembly.getStressAt256() * getExcitation();
	}

	@Override
	public float calculateStressApplied() {
		float impact = getEffectiveStressAt256() / 256f;
		this.lastStressApplied = impact;
		return impact;
	}

	private void updateStress() {
		if (level == null || level.isClientSide)
			return;
		if (hasNetwork())
			getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
		notifyUpdate();
	}

	public int getEnergyProductionRate(float rpm) {
		return (int) (RotorAssembly.baseEnergyRate(getEffectiveStressAt256(), rpm) * CommonConfig.GENERATOR_TERMINAL_EFFICIENCY.get());
	}

	private boolean isGenerating() {
		return getSpeed() != 0 && isSpeedRequirementFulfilled();
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide)
			return;
		if (firstTick) {
			firstTick = false;
			updateCache();
			if (rescan())
				return;
		}

		if (isGenerating())
			energy.internalProduceEnergy(getEnergyProductionRate(getSpeed()));

		for (Direction side : Direction.values()) {
			if (!RotorAssemblyControllerBlock.isEnergySide(getBlockState(), side))
				continue;
			BlockCapabilityCache<IEnergyStorage, Direction> sideCache = cache.get(side);
			IEnergyStorage target = sideCache == null ? null : sideCache.getCapability();
			if (target == null)
				continue;
			int extracted = energy.extractEnergy(target.receiveEnergy(CommonConfig.GENERATOR_TERMINAL_MAX_OUTPUT.get(), true), false);
			target.receiveEnergy(extracted, false);
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level != null && !level.isClientSide)
			rescan();
	}

	// returns true if this block was broken because another controller owns the chain.
	private boolean rescan() {
		switch (assembly.scan()) {
			case CONFLICT -> {
				level.destroyBlock(worldPosition, true);
				return true;
			}
			case CHANGED -> updateStress();
			default -> {}
		}
		return false;
	}

	private void updateCache() {
		if (!(level instanceof ServerLevel serverLevel))
			return;
		for (Direction side : Direction.values()) {
			cache.put(side, BlockCapabilityCache.create(
					Capabilities.EnergyStorage.BLOCK,
					serverLevel,
					worldPosition.relative(side),
					side.getOpposite(),
					() -> !this.isRemoved(),
					() -> {}
			));
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		assembly.addToGoggleTooltip(tooltip);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.production").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" " + Util.format(isGenerating() ? getEnergyProductionRate(getSpeed()) : 0) + "⚡/t ")
				.withStyle(ChatFormatting.AQUA).append(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY))).forGoggles(tooltip);
		return true;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		energy.read(tag);
		assembly.read(tag);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		energy.write(tag);
		assembly.write(tag);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void tickAudio() {
		super.tickAudio();
		float speed = Math.abs(getSpeed());
		if (speed == 0 || !isSpeedRequirementFulfilled() || !assembly.hasPower())
			return;
		float pitch = Mth.clamp((speed / 256f) + .5f, .5f, 1.5f);
		if (CommonConfig.AUDIO_ENABLED.get())
			CASoundScapes.play(AmbienceGroup.DYNAMO, worldPosition, pitch);
	}
}
