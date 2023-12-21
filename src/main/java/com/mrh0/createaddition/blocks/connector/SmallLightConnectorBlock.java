package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.energy.NodeRotation;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.shapes.CAShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallLightConnectorBlock extends AbstractConnectorBlock<SmallLightConnectorBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final VoxelShaper CONNECTOR_SHAPE = CAShapes.shape(6, 0, 6, 10, 5, 10).add(5, 4, 5, 11, 10, 11).forDirectional();
    public SmallLightConnectorBlock(Properties properties) {
        super(properties);
        FabricBlockSettings.copyOf(properties).luminance((blockState)->blockState.getValue(POWERED)?15:0);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return super.getStateForPlacement(c).setValue(POWERED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, NodeRotation.ROTATION, VARIANT, POWERED);
    }

    @Override
    public Class<SmallLightConnectorBlockEntity> getBlockEntityClass() {
        return SmallLightConnectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmallLightConnectorBlockEntity> getBlockEntityType() {
        return CATileEntities.SMALL_LIGHT_CONNECTOR.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CATileEntities.SMALL_LIGHT_CONNECTOR.create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CONNECTOR_SHAPE.get(state.getValue(FACING).getOpposite());

    }
}
