package com.mrh0.createaddition.blocks.rotor;

import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class RotorAssemblyControllerBlock extends RotatedPillarKineticBlock {

	public static final VoxelShaper SHAPE = CAShapes.shape(1, 1, 1, 15, 15, 15).add(6, 0, 6, 10, 16, 10).forAxis();

	public RotorAssemblyControllerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE.get(state.getValue(AXIS));
	}

	@Override
	public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(AXIS);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(AXIS);
	}

	/** Energy may only enter/leave through the faces perpendicular to the shaft. */
	public static boolean isEnergySide(BlockState state, Direction side) {
		return side == null || side.getAxis() != state.getValue(AXIS);
	}
}
