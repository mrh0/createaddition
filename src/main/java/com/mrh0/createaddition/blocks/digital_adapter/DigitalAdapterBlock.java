package com.mrh0.createaddition.blocks.digital_adapter;

import com.mrh0.createaddition.index.CATileEntities;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalAdapterBlock extends Block implements ITE<DigitalAdapterTileEntity>, IWrenchable {
    public DigitalAdapterBlock(Properties props) {
        super(props);
    }

    @Override
    public Class<DigitalAdapterTileEntity> getTileEntityClass() {
        return DigitalAdapterTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends DigitalAdapterTileEntity> getTileEntityType() {
        return CATileEntities.DIGITAL_ADAPTER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CATileEntities.DIGITAL_ADAPTER.create(pos, state);
    }
}
