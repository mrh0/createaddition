package com.mrh0.createaddition.shapes;

import static net.minecraft.util.Direction.UP;
import java.util.function.BiFunction;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class CAShapes {
	
	public static final VoxelShaper ELECTRIC_MOTOR = shape(2, 0, 6, 4, 16, 14).forAxis();
	
	private static Builder shape(VoxelShape shape) {
		return new Builder(shape);
	}

	// From create:AllShapes
	private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
		return shape(cuboid(x1, y1, z1, x2, y2, z2));
	}

	private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Block.makeCuboidShape(x1, y1, z1, x2, y2, z2);
	}

	private static class Builder {
		VoxelShape shape;

		public Builder(VoxelShape shape) {
			this.shape = shape;
		}

		Builder add(VoxelShape shape) {
			this.shape = VoxelShapes.or(this.shape, shape);
			return this;
		}

		Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
			return add(cuboid(x1, y1, z1, x2, y2, z2));
		}

		Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
			this.shape =
				VoxelShapes.combineAndSimplify(shape, cuboid(x1, y1, z1, x2, y2, z2), IBooleanFunction.ONLY_FIRST);
			return this;
		}

		VoxelShape build() {
			return shape;
		}

		VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
			return factory.apply(shape, direction);
		}

		VoxelShaper build(BiFunction<VoxelShape, Axis, VoxelShaper> factory, Axis axis) {
			return factory.apply(shape, axis);
		}

		VoxelShaper forDirectional(Direction direction) {
			return build(VoxelShaper::forDirectional, direction);
		}

		VoxelShaper forAxis() {
			return build(VoxelShaper::forAxis, Axis.Y);
		}

		VoxelShaper forHorizontalAxis() {
			return build(VoxelShaper::forHorizontalAxis, Axis.Z);
		}

		VoxelShaper forHorizontal(Direction direction) {
			return build(VoxelShaper::forHorizontal, direction);
		}

		VoxelShaper forDirectional() {
			return forDirectional(UP);
		}

	}
}
