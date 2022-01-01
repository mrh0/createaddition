package com.mrh0.createaddition.blocks.rotor;

import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.components.press.MechanicalPressTileEntity;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction.Axis;
import net.minecraft.world.IBlockReader;

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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return null;
	}

}
