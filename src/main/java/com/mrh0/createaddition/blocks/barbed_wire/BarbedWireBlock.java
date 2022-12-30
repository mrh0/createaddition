package com.mrh0.createaddition.blocks.barbed_wire;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

public class BarbedWireBlock extends Block implements net.minecraftforge.common.IForgeShearable {
	public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	private static DamageSource DMG_SOURCE = new DamageSource("barbed_wire");
	
	public BarbedWireBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, false).setValue(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		double delta = Math.abs(entity.getX() - entity.xOld) + Math.abs(entity.getY() - entity.yOld) + Math.abs(entity.getZ() - entity.zOld);
		if((entity instanceof LivingEntity) && delta > 0d) {
			entity.hurt(DMG_SOURCE, 2f);
		}
		entity.makeStuckInBlock(state, new Vec3(0.25D, (double)0.05F, 0.25D));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(VERTICAL, HORIZONTAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext c) {
		if(c.getClickedFace().getAxis() == Axis.Y)
			return defaultBlockState().setValue(HORIZONTAL_FACING, c.getPlayer().isShiftKeyDown() ? c.getHorizontalDirection().getClockWise() : c.getHorizontalDirection()).setValue(VERTICAL, false);
		else
			return defaultBlockState().setValue(HORIZONTAL_FACING, c.getClickedFace().getOpposite()).setValue(VERTICAL, true);
	}
}
