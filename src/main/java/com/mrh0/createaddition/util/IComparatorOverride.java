package com.mrh0.createaddition.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IComparatorOverride {
	public int getComparetorOverride();
	
	public static int getComparetorOverride(World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if(te != null) {
			if(te instanceof IComparatorOverride) {
				return ((IComparatorOverride)te).getComparetorOverride();
			}
		}
		return 5;
	}
}
