package com.mrh0.createaddition.blocks.creative_energy;

import com.mrh0.createaddition.energy.CreativeEnergyStorage;
import com.simibubi.create.content.logistics.block.inventories.CrateTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class CreativeEnergyTileEntity  extends CrateTileEntity {

	protected final CreativeEnergyStorage energy;
	private LazyOptional<IEnergyStorage> lazyEnergy;
	
	public CreativeEnergyTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		energy = new CreativeEnergyStorage();
		lazyEnergy = LazyOptional.of(() -> energy);
		setLazyTickRate(20);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityEnergy.ENERGY && !world.isRemote)
			return lazyEnergy.cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		if(world.isRemote())
			return;
		for(Direction side : Direction.values())
			energy.outputToSide(world, pos, side);
	}
	
	@Override
	public void remove() {
		super.remove();
		lazyEnergy.invalidate();
	}
}
