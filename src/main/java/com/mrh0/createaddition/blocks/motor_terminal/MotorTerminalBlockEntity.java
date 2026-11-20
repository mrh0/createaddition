package com.mrh0.createaddition.blocks.motor_terminal;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.rotor.IRotorAssemblyController;
import com.mrh0.createaddition.blocks.rotor.RotorAssembly;
import com.mrh0.createaddition.blocks.rotor.RotorAssemblyControllerBlock;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class MotorTerminalBlockEntity extends GeneratingKineticBlockEntity implements IRotorAssemblyController {

	protected ScrollValueBehaviour generatedSpeed;
	protected final InternalEnergyStorage energy;
	private final RotorAssembly assembly = new RotorAssembly(this);
	private boolean active = false;
	private boolean firstTick = true;

	public MotorTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(CommonConfig.MOTOR_TERMINAL_CAPACITY.get(), CommonConfig.MOTOR_TERMINAL_MAX_INPUT.get(), 0);
		setLazyTickRate(10);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				CABlockEntities.MOTOR_TERMINAL.get(),
				(be, side) -> RotorAssemblyControllerBlock.isEnergySide(be.getBlockState(), side) ? be.energy : null
		);
	}

	@Override
	public RotorAssembly getAssembly() {
		return assembly;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform(
				(state, side) -> side.getAxis() != state.getValue(RotorAssemblyControllerBlock.AXIS));
		generatedSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("generic.speed"), this, slot);
		generatedSpeed.between(-CommonConfig.MOTOR_TERMINAL_RPM_RANGE.get(), CommonConfig.MOTOR_TERMINAL_RPM_RANGE.get());
		generatedSpeed.value = 32;
		generatedSpeed.withCallback(i -> updateGeneratedRotation());
		behaviours.add(generatedSpeed);
	}

	@Override
	public float calculateAddedStressCapacity() {
		// assembly is null while SmartBlockEntity's constructor is still running.
		float capacity = assembly == null ? 0 : assembly.getStressAt256() / 256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.MOTOR_TERMINAL.has(getBlockState()) || generatedSpeed == null)
			return 0;
		return active && assembly != null && assembly.hasPower() ? generatedSpeed.getValue() : 0;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	public int getEnergyConsumptionRate() {
		if (generatedSpeed == null || !assembly.hasPower())
			return 0;
		int rpm = Math.abs(generatedSpeed.getValue());
		if (rpm == 0)
			return 0;
		double rate = RotorAssembly.baseEnergyRate(assembly.getStressAt256(), rpm) * CommonConfig.MOTOR_TERMINAL_CONSUMPTION_RATIO.get();
		return Math.max(1, (int) Math.ceil(rate));
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide)
			return;
		if (firstTick) {
			firstTick = false;
			if (rescan())
				return;
		}

		int consumption = getEnergyConsumptionRate();
		if (!active) {
			if (consumption > 0 && energy.getEnergyStored() > consumption * 2) {
				active = true;
				updateGeneratedRotation();
			}
		} else {
			int consumed = energy.internalConsumeEnergy(consumption);
			if (consumption == 0 || consumed < consumption) {
				active = false;
				updateGeneratedRotation();
			}
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
			case CHANGED -> {
				updateGeneratedRotation();
				notifyUpdate();
			}
			default -> {}
		}
		return false;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		assembly.addToGoggleTooltip(tooltip);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" " + Util.format(active ? getEnergyConsumptionRate() : 0) + "⚡/t ")
				.withStyle(ChatFormatting.AQUA).append(CreateLang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY))).forGoggles(tooltip);
		return true;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		energy.read(tag);
		assembly.read(tag);
		active = tag.getBoolean("active");
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		energy.write(tag);
		assembly.write(tag);
		tag.putBoolean("active", active);
	}

	@Override
	public void tickAudio() {
		super.tickAudio();
		if (!active)
			return;
		if (CommonConfig.AUDIO_ENABLED.get())
			CASoundScapes.play(CASoundScapes.AmbienceGroup.DYNAMO, worldPosition, 1);
	}
}
