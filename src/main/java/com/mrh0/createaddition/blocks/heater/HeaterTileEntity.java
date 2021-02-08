package com.mrh0.createaddition.blocks.heater;

import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.mixin.AbstractFurnaceMixin;
import com.simibubi.create.AllBlocks;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;

public class HeaterTileEntity extends BaseElectricTileEntity {
	
	public AbstractFurnaceTileEntity  cache;
	private boolean isFurnaceEngine = false;
	public static final int CONSUMPTION = 30, CONSUMPTION_ENGINE = 400;
	public static final boolean ALLOW_ENGINE = false;
	private boolean litState = false;
	
	private static final int 
	MAX_IN = 8196,
	MAX_OUT = 0,
	CAPACITY = 16000;
	
	public HeaterTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side == getBlockState().get(HeaterBlock.FACING).getOpposite();
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(world.isRemote())
			return;
		if(cache == null)
			return;
		IIntArray data = ((AbstractFurnaceMixin)cache).getFurnaceData();
		
		if(hasEnoughEnergy()) {
			data.set(0, Math.min(200, data.get(0)+2));
			if(!litState)
				updateState(true);
			litState = true;
		}
		else if(litState && data.get(0) < 1) {
			updateState(false);
			litState = false;
		}
	}
	
	public void refreshCache() {
		Direction d = getBlockState().get(HeaterBlock.FACING);
		TileEntity te = world.getTileEntity(pos.offset(d));
		if(te instanceof AbstractFurnaceTileEntity)
			cache = (AbstractFurnaceTileEntity) te;
		else
			cache = null;
		isFurnaceEngine = hasFurnaceEngine();
	}
	
	public boolean hasEnoughEnergy() {
		if(!ALLOW_ENGINE && isFurnaceEngine)
			return false;
		return energy.getEnergyStored() > (isFurnaceEngine ? CONSUMPTION_ENGINE * 20 : CONSUMPTION * 20);
	}
	
	public boolean hasFurnaceEngine() {
		Direction dir = getBlockState().get(HeaterBlock.FACING);
		BlockPos origin = pos.offset(dir);
		for(Direction d : Direction.values())
			if(world.getBlockState(origin.offset(d)).getBlock() == AllBlocks.FURNACE_ENGINE.get())
				return true;
		return false;
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		if(hasEnoughEnergy())
			energy.internalConsumeEnergy(isFurnaceEngine ? CONSUMPTION_ENGINE * 20 : CONSUMPTION * 20);
	}
	
	public void updateState(boolean lit) {
		Direction d = getBlockState().get(HeaterBlock.FACING);
		BlockState state = world.getBlockState(pos.offset(d));
		if(state.getBlock() instanceof AbstractFurnaceBlock) {
			if(state.get(AbstractFurnaceBlock.LIT) != lit)
				world.setBlockState(pos.offset(d), state.with(AbstractFurnaceBlock.LIT, lit));
		}
	}
	
	@Override
	public void firstTick() {
		super.firstTick();
		refreshCache();
	}
}
