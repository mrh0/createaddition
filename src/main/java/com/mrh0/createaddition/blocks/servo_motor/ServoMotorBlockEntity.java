package com.mrh0.createaddition.blocks.servo_motor;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.electric_motor.ElectricMotorBlockEntity;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CALang;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.contraptions.IControlContraption.RotationMode;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.core.Direction.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ServoMotorBlockEntity extends MechanicalBearingBlockEntity {

	static final BehaviourType<ServoMovementMode> MOVEMENT_MODE_TYPE = new BehaviourType<>();

	// ScrollOptionBehaviour and KineticScrollValueBehaviour both inherit ScrollValueBehaviour.TYPE,
	// so they'd overwrite each other in SmartBlockEntity's BehaviourType→Behaviour map. This
	// subclass gets its own type, a distinct NBT key, and a distinct netId — three separate
	// conflicts that all need to be resolved for two scroll behaviours to coexist.
	static class ServoMovementMode extends ScrollOptionBehaviour<RotationMode> {
		ServoMovementMode(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
			super(RotationMode.class, label, be, slot);
		}

		@Override
		public BehaviourType<?> getType() {
			return MOVEMENT_MODE_TYPE;
		}

		// ValueSettingsPacket matches behaviours by netId(); generatedSpeed uses 0 (default).
		@Override
		public int netId() {
			return 1;
		}

		@Override
		public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
			nbt.putInt("RotationMode", value);
		}

		@Override
		public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
			value = nbt.getInt("RotationMode");
		}
	}

	static final BehaviourType<ServoAngleLimit> MIN_ANGLE_TYPE = new BehaviourType<>();
	static final BehaviourType<ServoAngleLimit> MAX_ANGLE_TYPE = new BehaviourType<>();

	// Same three-conflict pattern as ServoMovementMode: distinct BehaviourType, distinct NBT key,
	// distinct netId. The negated flag controls whether the angle is treated as negative
	// (min side, 0 to -180) or positive (max side, 0 to +180).
	static class ServoAngleLimit extends ScrollValueBehaviour {
		private final BehaviourType<ServoAngleLimit> myType;
		private final int myNetId;
		private final String nbtKey;
		private final int defaultVal;
		private final boolean negated;

		ServoAngleLimit(Component label, SmartBlockEntity be, ValueBoxTransform slot,
				BehaviourType<ServoAngleLimit> type, int netId, String nbtKey, int defaultVal, boolean negated) {
			super(label, be, slot);
			this.myType = type;
			this.myNetId = netId;
			this.nbtKey = nbtKey;
			this.defaultVal = defaultVal;
			this.negated = negated;
			between(0, 180);
			value = defaultVal;
		}

		/** Returns the signed angle in degrees this limit represents. */
		public float getAngle() {
			return negated ? -value : value;
		}

		@Override
		public BehaviourType<?> getType() { return myType; }

		@Override
		public int netId() { return myNetId; }

		@Override
		public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
			ValueSettingsFormatter fmt = new ValueSettingsFormatter(
				vs -> Component.literal((negated ? -vs.value() : vs.value()) + "°"));
			return new ValueSettingsBoard(label, 180, 5, ImmutableList.of(Component.literal("°")), fmt);
		}

		@Override
		public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
			nbt.putInt(nbtKey, value);
		}

		@Override
		public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
			// nbt.getInt() returns 0 for a missing key — fall back to the constructor default.
			value = nbt.contains(nbtKey) ? nbt.getInt(nbtKey) : defaultVal;
		}
	}

	protected float motorSpeed;
	protected ScrollValueBehaviour generatedSpeed;
	protected ServoAngleLimit minAngle;
	protected ServoAngleLimit maxAngle;
	protected final InternalEnergyStorage energy;
	private final IEnergyStorage energyCapability;

	private boolean active = false;
	private boolean firstTick = true;
	private boolean allowAssemble = false;
	private int lastNetSignal = Integer.MIN_VALUE;
	private float targetAngle = 0;

	public ServoMotorBlockEntity(BlockEntityType<? extends ServoMotorBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		energy = new InternalEnergyStorage(CommonConfig.ELECTRIC_MOTOR_CAPACITY.get(),
				CommonConfig.ELECTRIC_MOTOR_MAX_INPUT.get(), 0);
		energyCapability = energy;
		setLazyTickRate(20);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				CABlockEntities.SERVO_MOTOR.get(),
				(be, context) -> be.energyCapability);
	}

	// Movement mode shows on the two horizontal sides perpendicular to FACING.
	// For a vertical FACING (UP/DOWN), NORTH and SOUTH are used.
	private static boolean isModeSide(BlockState state, Direction d) {
		Axis facingAxis = state.getValue(ServoMotorBlock.FACING).getAxis();
		Axis dAxis = d.getAxis();
		if (facingAxis == Axis.Y) return dAxis == Axis.Z;
		return dAxis != facingAxis && dAxis != Axis.Y;
	}

	// Max angle on the UP face (EAST for vertical servos).
	private static boolean isMaxAngleSide(BlockState state, Direction d) {
		if (state.getValue(ServoMotorBlock.FACING).getAxis() == Axis.Y) return d == Direction.EAST;
		return d == Direction.UP;
	}

	// Min angle on the DOWN face (WEST for vertical servos).
	private static boolean isMinAngleSide(BlockState state, Direction d) {
		if (state.getValue(ServoMotorBlock.FACING).getAxis() == Axis.Y) return d == Direction.WEST;
		return d == Direction.DOWN;
	}

	@Override
	public ValueBoxTransform getMovementModeSlot() {
		return new DirectionalExtenderScrollOptionSlot(ServoMotorBlockEntity::isModeSide);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		// super's movementMode uses ScrollValueBehaviour.TYPE — same as generatedSpeed's type,
		// so one would silently overwrite the other in SmartBlockEntity's type→behaviour map.
		// Replace it in-place with our ServoMovementMode that has its own distinct type.
		for (int i = 0; i < behaviours.size(); i++) {
			if (behaviours.get(i) == movementMode) {
				movementMode = new ServoMovementMode(
						CreateLang.translateDirect("contraptions.movement_mode"), this, getMovementModeSlot());
				behaviours.set(i, movementMode);
				break;
			}
		}

		CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform(
				(motor, side) -> motor.getValue(ServoMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("generic.speed"), this, slot);
		generatedSpeed.between(-CommonConfig.ELECTRIC_MOTOR_RPM_RANGE.get(),
				CommonConfig.ELECTRIC_MOTOR_RPM_RANGE.get());
		generatedSpeed.value = 32;
		generatedSpeed.withCallback(rpm -> {
			motorSpeed = rpm;
			updateGeneratedRotation();
		});
		behaviours.add(generatedSpeed);

		// Max angle: 0 to +180°, shown on the UP face. Default 90°.
		maxAngle = new ServoAngleLimit(Component.literal("Max Angle"), this,
				new DirectionalExtenderScrollOptionSlot(ServoMotorBlockEntity::isMaxAngleSide),
				MAX_ANGLE_TYPE, 2, "MaxAngle", 90, false);
		behaviours.add(maxAngle);

		// Min angle: 0 to -180°, shown on the DOWN face. Default -90° (stored as 90).
		minAngle = new ServoAngleLimit(Component.literal("Min Angle"), this,
				new DirectionalExtenderScrollOptionSlot(ServoMotorBlockEntity::isMinAngleSide),
				MIN_ANGLE_TYPE, 3, "MinAngle", 90, true);
		behaviours.add(minAngle);
	}

	// When assembled, delegate directly to the contraption entity's own lerp so the disc
	// is always in perfect sync with the rotating contraption blocks.
	// Returns the static angle when not assembled — disc stays fixed before assembly.
	@Override
	public float getInterpolatedAngle(float partialTicks) {
		if (!running) return angle;
		if (movedContraption != null)
			return movedContraption.getAngle(partialTicks + 1f);
		return angle;
	}

	// When the contraption is assembled, the angle is set directly by the redstone
	// signal every tick — continuous spinning is not used.
	@Override
	public float getAngularSpeed() {
		if (running) return 0;
		return super.getAngularSpeed();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.SERVO_MOTOR.has(getBlockState())) return 0;
		return convertToDirection(active ? motorSpeed : 0, getBlockState().getValue(ServoMotorBlock.FACING));
	}

	@Override
	public float calculateAddedStressCapacity() {
		float capacity = CommonConfig.MAX_STRESS.get() / 256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}

	@Override
	protected Block getStressConfigKey() {
		return CABlocks.SERVO_MOTOR.get();
	}

	@Override
	public void assemble() {
		// MechanicalBearingBlockEntity.tick() calls assemble() whenever assembleNextTick is set,
		// which happens on every kinetic speed change. Guard here so the contraption only builds
		// on an explicit right-click (triggerAssemble sets allowAssemble before setting the flag).
		if (!allowAssemble) return;
		allowAssemble = false;

		if (!(level.getBlockState(worldPosition).getBlock() instanceof ServoMotorBlock))
			return;

		Direction direction = getBlockState().getValue(ServoMotorBlock.FACING);
		BearingContraption contraption = new BearingContraption(false, direction);
		try {
			if (!contraption.assemble(level, worldPosition))
				return;
			lastException = null;
		} catch (AssemblyException e) {
			lastException = e;
			sendData();
			return;
		}

		contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
		movedContraption = ControlledContraptionEntity.create(level, this, contraption);
		BlockPos anchor = worldPosition.relative(direction);
		movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
		movedContraption.setRotationAxis(direction.getAxis());
		level.addFreshEntity(movedContraption);

		AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

		if (contraption.containsBlockBreakers())
			award(AllAdvancements.CONTRAPTION_ACTORS);

		running = true;
		angle = 0;
		lastNetSignal = Integer.MIN_VALUE; // force tick() to re-evaluate redstone on next tick
		sendData();
		updateGeneratedRotation();
	}

	@Override
	public void tick() {
		if (firstTick) {
			motorSpeed = generatedSpeed.getValue();
			firstTick = false;
		}

		if (!level.isClientSide()) {
			int con = ElectricMotorBlockEntity.getEnergyConsumptionRate(motorSpeed);
			if (!running) {
				// No energy consumed when not assembled; activate whenever the buffer is sufficient
				boolean hasEnergy = energy.getEnergyStored() >= con;
				if (active != hasEnergy) {
					active = hasEnergy;
					updateGeneratedRotation();
				}
			} else {
				// Assembled: full rate when the angle is stepping, 10% when stationary at target
				boolean moving = Math.abs(targetAngle - angle) > 0.001f && Math.abs(motorSpeed) > 0f;
				int consumption = moving ? con : con / 10;
				if (!active) {
					if (energy.getEnergyStored() >= con) {
						active = true;
						updateGeneratedRotation();
					}
				} else {
					if (energy.internalConsumeEnergy(consumption) < consumption) {
						active = false;
						updateGeneratedRotation();
					}
				}
			}
		}

		super.tick();

		// Net redstone = maxFaceSignal - minFaceSignal, range -15 to +15.
		// Net 0 → center (angle 0), positive → toward maxAngle, negative → toward minAngle.
		if (!level.isClientSide && active) {
			Direction facing = getBlockState().getValue(ServoMotorBlock.FACING);
			Direction maxFace = facing.getAxis() == Axis.Y ? Direction.EAST : Direction.UP;
			Direction minFace = facing.getAxis() == Axis.Y ? Direction.WEST : Direction.DOWN;
			int maxSignal = level.getSignal(worldPosition.relative(maxFace), maxFace);
			int minSignal = level.getSignal(worldPosition.relative(minFace), minFace);
			int net = maxSignal - minSignal;
			if (net != lastNetSignal) {
				lastNetSignal = net;
				// minAngle.getAngle() already returns a negative value (negated=true)
				targetAngle = net >= 0
					? (net / 15.0f) * maxAngle.getAngle()
					: (-net / 15.0f) * minAngle.getAngle();
				// Auto-assemble on non-zero signal, auto-disassemble to center handled below
				if (net != 0 && !running)
					triggerAssemble();
				sendData();
			}

			// Step angle and drive contraption only when assembled
			if (running) {
				float diff = targetAngle - angle;
				float degreesPerTick = Math.abs(motorSpeed) * 360f / 1200f;
				if (Math.abs(diff) > 0.001f && degreesPerTick > 0f) {
					angle += Math.signum(diff) * Math.min(Math.abs(diff), degreesPerTick);
					applyRotation();
					sendData();
				}
				int mode = movementMode.getValue();
				if (mode == RotationMode.ROTATE_PLACE.ordinal()) {
					// "Place when stopped": snap to nearest 90° and disassemble at min/max limit
					if (Math.abs(angle - maxAngle.getAngle()) < 0.1f
							|| Math.abs(angle - minAngle.getAngle()) < 0.1f) {
						angle = Math.round(angle / 90f) * 90f;
						applyRotation();
						disassemble();
					}
				} else if (mode == RotationMode.ROTATE_PLACE_RETURNED.ordinal()) {
					// "Place near initial angle": disassemble only when back near 0°
					if (Math.abs(angle) < 0.1f)
						disassemble();
				}
			}
		}
	}

	@Override
	public void tickAudio() {
		super.tickAudio();
		if (!active || !running || getAngularSpeed() == 0) return;
		if (CommonConfig.AUDIO_ENABLED.get())
			CASoundScapes.play(CASoundScapes.AmbienceGroup.DYNAMO, worldPosition, 1);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		CALang.builder()
				.add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption")
						.withStyle(ChatFormatting.GRAY))
				.forGoggles(tooltip);
		CALang.builder()
				.add(Component.literal(
						" " + Util.format(ElectricMotorBlockEntity.getEnergyConsumptionRate(generatedSpeed.getValue()))
								+ "⚡/t ")
						.withStyle(ChatFormatting.AQUA)
						.append(CreateLang.translateDirect("gui.goggles.at_current_speed")
								.withStyle(ChatFormatting.DARK_GRAY)))
				.forGoggles(tooltip);
		return true;
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		if (!clientPacket)
			energy.write(compound);
		compound.putBoolean("servo_active", active);
		compound.putFloat("TargetAngle", targetAngle);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (!clientPacket)
			energy.read(compound);
		active = compound.getBoolean("servo_active");
		if (compound.contains("TargetAngle"))
			targetAngle = compound.getFloat("TargetAngle");
		// The parent's client-side read resets angle = angleBefore so kinetic-speed
		// extrapolation works for normal bearings. We use explicit server-sent angles, so
		// restore the actual server angle for correct applyRotation() calls each client tick.
		if (clientPacket && running)
			angle = compound.getFloat("Angle");
	}

	public void triggerAssemble() {
		allowAssemble = true;
		assembleNextTick = true;
	}

	public float getRPM() {
		return motorSpeed;
	}

	public int getEnergyConsumption() {
		return ElectricMotorBlockEntity.getEnergyConsumptionRate(motorSpeed);
	}
}
