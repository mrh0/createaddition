package com.mrh0.createaddition.index;

import com.mrh0.createaddition.blocks.treated_gearbox.*;
import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.repack.registrate.util.entry.TileEntityEntry;

public class CAIETileEntities {
	public static final TileEntityEntry<TreatedGearboxTileEntity> TREATED_GEARBOX = CreateAddition.registrate()
			.tileEntity("treated_gearbox", TreatedGearboxTileEntity::new)
			.instance(() -> HalfShaftInstance::new)
			.validBlocks(CAIEBlocks.TREATED_GEARBOX)
			.renderer(() -> TreatedGearboxRenderer::new)
			.register();
	
	public static void register() {}
}
