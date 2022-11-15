package com.mrh0.createaddition.blocks.rotor;

import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Rotor extends HorizontalKineticBlock implements ITE<MechanicalPressTileEntity> {

	public Rotor(Properties properties) {
		super(properties);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return null;
	}

	@Override
	public Class<MechanicalPressTileEntity> getTileEntityClass() {
		return null;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return null;
	}
	
	@Override
	public BlockEntityType<? extends MechanicalPressTileEntity> getTileEntityType() {
		return null;
	}

}
