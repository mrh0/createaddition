package com.mrh0.createaddition.energy;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.minecraft.core.Direction.Axis.*;
import static net.minecraft.world.level.block.Rotation.*;

/**
 * A hacky, messy, and probably not the best solution to a problem making my
 * head spin.
 *
 * Used to recalculate the relative positions of wires & nodes.
 */
public enum NodeRotation implements StringRepresentable {

	NONE(null, null),

	Y_CLOCKWISE_90(Y, CLOCKWISE_90),
	Y_CLOCKWISE_180(Y, CLOCKWISE_180),
	Y_COUNTERCLOCKWISE_90(Y, COUNTERCLOCKWISE_90),

	X_CLOCKWISE_90(X, CLOCKWISE_90),
	X_CLOCKWISE_180(X, CLOCKWISE_180),
	X_COUNTERCLOCKWISE_90(X, COUNTERCLOCKWISE_90),

	Z_CLOCKWISE_90(Z, CLOCKWISE_90),
	Z_CLOCKWISE_180(Z, CLOCKWISE_180),
	Z_COUNTERCLOCKWISE_90(Z, COUNTERCLOCKWISE_90);

	public static final EnumProperty<NodeRotation> ROTATION = EnumProperty.create("rotation", NodeRotation.class);
	public static final NodeRotation[] VALUES = values();

	final Direction.Axis axis;
	final Rotation rotation;

	NodeRotation(Direction.Axis axis, Rotation rotation) {
		this.axis = axis;
		this.rotation = rotation;
	}

	public Direction rotate(Direction current, boolean handleY) {
		if (this == NONE) return current;
		if (!handleY && this.axis == Y && current.getAxis() == Y) return current;
		if (this.rotation == COUNTERCLOCKWISE_90) return current.getCounterClockWise(this.axis);
		if (this.rotation == CLOCKWISE_90) return current.getClockWise(this.axis);
		return current.getOpposite();
	}

	public Vec3i updateRelative(Vec3i current) {
		// Calculate the new relative position in a left-handed coordinate system.
		if (this == NONE) return current;
		if (this.axis == Y) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(-current.getZ(), current.getY(), current.getX());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(-current.getX(), current.getY(), -current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(current.getZ(), current.getY(), -current.getX());
		}
		if (this.axis == X) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(current.getX(), current.getZ(), -current.getY());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(current.getX(), -current.getY(), -current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(current.getX(), -current.getZ(), current.getY());
		}
		if (this.axis == Z) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(current.getY(), -current.getX(), current.getZ());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(-current.getX(), -current.getY(), current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(-current.getY(), current.getX(), current.getZ());
		}
		return current;
	}

	public Direction.Axis getAxis() {
		return axis;
	}

	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public @NotNull String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static NodeRotation get(Direction.Axis axis, Rotation rotation) {
		if (rotation == Rotation.NONE) return NONE;
		for (NodeRotation wr : VALUES) {
			if (wr.axis == axis && wr.rotation == rotation) return wr;
		}
		return NONE;
	}
}
