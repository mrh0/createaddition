package com.mrh0.createaddition.blocks.heater;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.item.Multimeter;
import com.mrh0.createaddition.mixin.AbstractFurnaceMixin;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class HeaterTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {
	
	public AbstractFurnaceTileEntity cache;
	private boolean isFurnaceEngine = false;
	public static final int CONSUMPTION = Config.HEATER_NORMAL_CONSUMPTION.get(),
			CONSUMPTION_ENGINE = Config.HEATER_FURNACE_ENGINE_CONSUMPTION.get();
	public static final boolean ALLOW_ENGINE = Config.HEATER_FURNACE_ENGINE_ENABLED.get();
	private boolean litState = false;
	
	private static final int 
	MAX_IN = Config.HEATER_MAX_INPUT.get(),
	MAX_OUT = 0,
	CAPACITY = Config.HEATER_CAPACITY.get();
	
	public HeaterTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, CAPACITY, MAX_IN, MAX_OUT);
		setLazyTickRate(20);
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side == getBlockState().getValue(HeaterBlock.FACING).getOpposite();
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}
	
	int timeout;
	
	@Override
	public void tick() {
		super.tick();
		if(level.isClientSide())
			return;
		if(cache == null)
			return;
		if(cache.isRemoved())
			cache = null;
		if(cache == null)
			return;
		
		/*if(CreateAddition.IE_ACTIVE) {
			litState =  IEHeaterOptional.externalHeater(cache, energy);
		}*/
		
		IIntArray data = ((AbstractFurnaceMixin)cache).getDataAccess();
		timeout--;
		if(timeout < 0)
			timeout = 0;
		if(hasEnoughEnergy()) {
			data.set(0, Math.min(200, data.get(0)+2));
			if(!litState)
				if(timeout <= 0)
					updateState(true);
		}
		else if(litState && data.get(0) < 1) {
			if(timeout <= 0)
				updateState(false);
		}
		
		// Old Lazy
		if(hasEnoughEnergy())
			energy.internalConsumeEnergy(getConsumption());
	}
	
	public void refreshCache() {
		Direction d = getBlockState().getValue(HeaterBlock.FACING);
		TileEntity te = level.getBlockEntity(worldPosition.relative(d));
		if(te instanceof AbstractFurnaceTileEntity)
			cache = (AbstractFurnaceTileEntity) te;
		else
			cache = null;
		isFurnaceEngine = hasFurnaceEngine();
	}
	
	public boolean hasEnoughEnergy() {
		if(!ALLOW_ENGINE && isFurnaceEngine)
			return false;
		return energy.getEnergyStored() > getConsumption();
	}
	
	public boolean hasFurnaceEngine() {
		Direction dir = getBlockState().getValue(HeaterBlock.FACING);
		BlockPos origin = worldPosition.relative(dir);
		for(Direction d : Direction.values())
			if(level.getBlockState(origin.relative(d)).getBlock() == AllBlocks.FURNACE_ENGINE.get())
				return true;
		return false;
	}
	
	public int getConsumption() {
		return (isFurnaceEngine ? CONSUMPTION_ENGINE : CONSUMPTION);
	}
	
	@Override
	public void lazyTick() {
		super.lazyTick();
		
		
		isFurnaceEngine = hasFurnaceEngine();
	}
	
	public void updateState(boolean lit) {
		timeout = 10;
		Direction d = getBlockState().getValue(HeaterBlock.FACING);
		BlockState state = level.getBlockState(worldPosition.relative(d));
		if(state.getBlock() instanceof AbstractFurnaceBlock) {
			if(state.getValue(AbstractFurnaceBlock.LIT) != lit)
				level.setBlockAndUpdate(worldPosition.relative(d), state.setValue(AbstractFurnaceBlock.LIT, lit));
		}
		causeBlockUpdate();
		litState = lit;
	}
	
	@Override
	public void firstTick() {
		super.firstTick();
		refreshCache();
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		//tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").formatted(TextFormatting.GRAY)));
		//tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").formatted(TextFormatting.AQUA)));
		
		tooltip.add(new StringTextComponent(spacing)
				.append(new TranslationTextComponent("block.createaddition.heater.info").withStyle(TextFormatting.WHITE)));
		
		if(isFurnaceEngine && !ALLOW_ENGINE)
			tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent("block.createaddition.heater.engine_heating_disabled").withStyle(TextFormatting.RED)));
		
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.format(hasEnoughEnergy() ? getConsumption() : 0) + "fe/t ")).withStyle(TextFormatting.AQUA));
		return true;
	}
}
