package com.mrh0.createaddition.blocks.digital_adapter;

import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalAdapterBlock extends Block implements IBE<DigitalAdapterBlockEntity>, IWrenchable {
    public DigitalAdapterBlock(Properties props) {
        super(props);
    }

    @Override
    public Class<DigitalAdapterBlockEntity> getBlockEntityClass() {
        return DigitalAdapterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DigitalAdapterBlockEntity> getBlockEntityType() {
        return CABlockEntities.DIGITAL_ADAPTER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CABlockEntities.DIGITAL_ADAPTER.create(pos, state);
    }
}
