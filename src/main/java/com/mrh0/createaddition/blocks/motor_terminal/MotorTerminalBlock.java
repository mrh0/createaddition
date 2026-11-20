package com.mrh0.createaddition.blocks.motor_terminal;

import com.mrh0.createaddition.blocks.rotor.RotorAssemblyControllerBlock;
import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class MotorTerminalBlock extends RotorAssemblyControllerBlock implements IBE<MotorTerminalBlockEntity> {

	public MotorTerminalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}

	@Override
	public Class<MotorTerminalBlockEntity> getBlockEntityClass() {
		return MotorTerminalBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MotorTerminalBlockEntity> getBlockEntityType() {
		return CABlockEntities.MOTOR_TERMINAL.get();
	}
}
