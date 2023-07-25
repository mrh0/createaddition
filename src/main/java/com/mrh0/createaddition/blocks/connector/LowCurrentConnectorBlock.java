package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.blocks.connector.base.AbstractConnectorBlock;
import com.mrh0.createaddition.index.CABlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LowCurrentConnectorBlock extends AbstractConnectorBlock<LowCurrentConnectorBlockEntity> {
    public LowCurrentConnectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<LowCurrentConnectorBlockEntity> getBlockEntityClass() {
        return LowCurrentConnectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LowCurrentConnectorBlockEntity> getBlockEntityType() {
        return CABlockEntities.LV_CONNECTOR.get();
    }
}
