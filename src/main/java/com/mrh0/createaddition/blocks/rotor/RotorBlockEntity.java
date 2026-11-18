package com.mrh0.createaddition.blocks.rotor;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RotorBlockEntity extends KineticBlockEntity {

	public final LerpedFloat visualSpeed = LerpedFloat.linear();
	public float angle;

	public RotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(1);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (clientPacket)
			visualSpeed.chase(getGeneratedSpeed(), 1 / 64f, Chaser.EXP);
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide)
			return;

		visualSpeed.updateChaseTarget(getSpeed());
		visualSpeed.tickChaser();
		angle += visualSpeed.getValue() * 3 / 10f;
		angle %= 360;
	}

	/** Interpolated body angle in degrees. */
	public float getRenderAngle(float partialTicks) {
		return angle + visualSpeed.getValue(partialTicks) * 3 / 10f * partialTicks;
	}
}
