package com.mrh0.createaddition.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IComparetorOverride {
	public int getComparetorOverride();
	
	public static int getComparetorOverride(Level worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te != null) {
			if(te instanceof IComparetorOverride) {
				return ((IComparetorOverride)te).getComparetorOverride();
			}
		}
		return 5;
	}
}
