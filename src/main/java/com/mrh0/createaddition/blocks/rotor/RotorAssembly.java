package com.mrh0.createaddition.blocks.rotor;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.stator.StatorBlock;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.config.CommonConfig;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RotorAssembly {

	/** Hard limit on how far a scan walks in each direction, regardless of config. */
	private static final int SCAN_LIMIT = 16;

	public enum ScanResult { UNCHANGED, CHANGED, CONFLICT }

	private final KineticBlockEntity be;
	private int rotors;
	private int stators;
	/** Game time of the controller's first server tick; -1 until then. Used to decide which controller survives. */
	private long placedTime = -1;

	public RotorAssembly(KineticBlockEntity be) {
		this.be = be;
	}

	public int getRotors() {
		return rotors;
	}

	public int getStators() {
		return stators;
	}

	public boolean hasPower() {
		return stators > 0;
	}

	/** Stress (SU at 256 RPM) of the whole assembly. Each stator unlocks 1/8 of a rotor's stress. */
	public float getStressAt256() {
		return CommonConfig.ROTOR_STRESS.get() * stators / (float) StatorBlock.RING_SIZE;
	}

	/** Base FE/t equivalent of the given stress at the given RPM, using the global conversion rate. */
	public static double baseEnergyRate(float stressAt256, float rpm) {
		int maxStress = CommonConfig.MAX_STRESS.get();
		if (maxStress <= 0)
			return 0;
		return CommonConfig.FE_RPM.get() * ((double) stressAt256 / maxStress) * (Math.abs(rpm) / 256d);
	}

	public ScanResult scan() {
		Level level = be.getLevel();
		if (level == null || level.isClientSide)
			return ScanResult.UNCHANGED;
		if (placedTime < 0)
			placedTime = level.getGameTime();

		BlockPos origin = be.getBlockPos();
		Axis axis = be.getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
		int maxRotors = CommonConfig.ROTOR_ASSEMBLY_MAX_ROTORS.get();
		int foundRotors = 0;
		int foundStators = 0;
		boolean conflict = false;

		for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
			Direction dir = Direction.fromAxisAndDirection(axis, axisDirection);
			BlockPos.MutableBlockPos pos = origin.mutable();
			for (int i = 0; i < SCAN_LIMIT; i++) {
				pos.move(dir);
				if (!level.isLoaded(pos))
					break;
				BlockState state = level.getBlockState(pos);
				if (StatorBlock.isRotor(state, axis)) {
					if (foundRotors < maxRotors) {
						foundRotors++;
						foundStators += StatorBlock.countRing(level, pos, axis);
					}
					continue;
				}
				if (isController(state, axis) && level.getBlockEntity(pos) instanceof IRotorAssemblyController other
						&& outranks(other.getAssembly(), pos))
					conflict = true;
				break;
			}
		}

		if (conflict)
			return ScanResult.CONFLICT;
		if (foundRotors == rotors && foundStators == stators)
			return ScanResult.UNCHANGED;
		rotors = foundRotors;
		stators = foundStators;
		return ScanResult.CHANGED;
	}

	/** True if the other controller should survive over this one (older, or same age with a lower position). */
	private boolean outranks(RotorAssembly other, BlockPos otherPos) {
		if (other.placedTime < 0)
			return false;
		if (other.placedTime != placedTime)
			return other.placedTime < placedTime;
		return otherPos.asLong() < be.getBlockPos().asLong();
	}

	private static boolean isController(BlockState state, Axis axis) {
		return state.getBlock() instanceof RotorAssemblyControllerBlock
				&& state.getValue(RotatedPillarKineticBlock.AXIS) == axis;
	}

	public void addToGoggleTooltip(List<Component> tooltip) {
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.rotor_assembly.info").withStyle(ChatFormatting.WHITE)).forGoggles(tooltip);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.rotor_assembly.rotors").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(" " + rotors).withStyle(ChatFormatting.AQUA))).forGoggles(tooltip);
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.rotor_assembly.stators").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(" " + stators + " / " + rotors * StatorBlock.RING_SIZE).withStyle(ChatFormatting.AQUA))).forGoggles(tooltip);
	}

	public void write(CompoundTag tag) {
		CompoundTag assembly = new CompoundTag();
		assembly.putInt("Rotors", rotors);
		assembly.putInt("Stators", stators);
		assembly.putLong("Placed", placedTime);
		tag.put("RotorAssembly", assembly);
	}

	public void read(CompoundTag tag) {
		CompoundTag assembly = tag.getCompound("RotorAssembly");
		rotors = assembly.getInt("Rotors");
		stators = assembly.getInt("Stators");
		placedTime = assembly.contains("Placed") ? assembly.getLong("Placed") : -1;
	}
}
