package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.index.CABlockEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.content.logistics.crate.CrateBlock;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CreativeEnergyBlock extends CrateBlock implements IBE<CreativeEnergyBlockEntity> {

	public static final VoxelShape CREATIVE_ENERGY_SHAPE = CAShapes.shape(1,0,1,15,16,15).add(0,2,0,16,14,16).build();
	
	public CreativeEnergyBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return CREATIVE_ENERGY_SHAPE;
	}

	@Override
	public Class<CreativeEnergyBlockEntity> getBlockEntityClass() {
		return CreativeEnergyBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CreativeEnergyBlockEntity> getBlockEntityType() {
		return CABlockEntities.CREATIVE_ENERGY.get();
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		if(tileentity != null) {
			if(tileentity instanceof CreativeEnergyBlockEntity) {
				((CreativeEnergyBlockEntity)tileentity).updateCache();
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.CREATIVE_ENERGY.create(pos, state);
	}
}
