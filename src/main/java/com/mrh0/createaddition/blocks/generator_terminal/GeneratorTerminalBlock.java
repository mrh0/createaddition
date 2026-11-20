package com.mrh0.createaddition.blocks.generator_terminal;

import com.mrh0.createaddition.blocks.rotor.RotorAssemblyControllerBlock;
import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class GeneratorTerminalBlock extends RotorAssemblyControllerBlock implements IBE<GeneratorTerminalBlockEntity> {

	public GeneratorTerminalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<GeneratorTerminalBlockEntity> getBlockEntityClass() {
		return GeneratorTerminalBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends GeneratorTerminalBlockEntity> getBlockEntityType() {
		return CABlockEntities.GENERATOR_TERMINAL.get();
	}
}
