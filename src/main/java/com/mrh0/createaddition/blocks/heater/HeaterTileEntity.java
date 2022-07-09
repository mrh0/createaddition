package com.mrh0.createaddition.blocks.heater;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.blocks.base.AbstractBurnerBlock;
import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.BaseElectricTileEntity;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HeaterTileEntity extends BaseElectricTileEntity implements IHaveGoggleInformation {
	
	public BlockEntity cache;
	private boolean isFurnaceEngine = false;
	public static final int CONSUMPTION = Config.HEATER_NORMAL_CONSUMPTION.get(),
			CONSUMPTION_ENGINE = Config.HEATER_FURNACE_ENGINE_CONSUMPTION.get();
	public static final boolean ALLOW_ENGINE = Config.HEATER_FURNACE_ENGINE_ENABLED.get();
	private boolean litState = false;
	
	private static final int 
	MAX_IN = Config.HEATER_MAX_INPUT.get(),
	MAX_OUT = 0,
	CAPACITY = Config.HEATER_CAPACITY.get();
	
	public HeaterTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state, CAPACITY, MAX_IN, MAX_OUT);
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
		
		timeout--;
		if(timeout < 0)
			timeout = 0;
		
		if(cache instanceof AbstractFurnaceBlockEntity) {
			AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) cache;
			ContainerData data = furnace.dataAccess;
			
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
		}
		else if(cache instanceof AbstractBurnerBlockEntity) {
			AbstractBurnerBlockEntity burner = (AbstractBurnerBlockEntity) cache;
			
			if(hasEnoughEnergy()) {
				burner.litTime = Math.min(200, burner.litTime+2);
				if(!litState)
					if(timeout <= 0)
						updateState(true);
			}
			else if(litState && burner.litTime < 1) {
				if(timeout <= 0)
					updateState(false);
			}
		}

		// Old Lazy
		if(hasEnoughEnergy())
			energy.internalConsumeEnergy(getConsumption());
	}
	
	public void refreshCache() {
		Direction d = getBlockState().getValue(HeaterBlock.FACING);
		BlockEntity te = level.getBlockEntity(worldPosition.relative(d));
		if(te instanceof AbstractFurnaceBlockEntity || te instanceof AbstractBurnerBlockEntity)
			cache = te;
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
		// Furnace Engine no longer exists in 0.5
		/*
		for(Direction d : Direction.values())
			if(level.getBlockState(origin.relative(d)).getBlock() == AllBlocks.FURNACE_ENGINE.get())
				return true;
		 */
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
		if(state.getBlock() instanceof AbstractBurnerBlock) {
			if(state.getValue(AbstractBurnerBlock.LIT) != lit)
				level.setBlockAndUpdate(worldPosition.relative(d), state.setValue(AbstractBurnerBlock.LIT, lit));
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
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		//tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").formatted(TextFormatting.GRAY)));
		//tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").formatted(TextFormatting.AQUA)));
		
		tooltip.add(new TextComponent(spacing)
				.append(new TranslatableComponent("block.createaddition.heater.info").withStyle(ChatFormatting.WHITE)));
		
		if(isFurnaceEngine && !ALLOW_ENGINE)
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("block.createaddition.heater.engine_heating_disabled").withStyle(ChatFormatting.RED)));
		
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" " + Util.format(hasEnoughEnergy() ? getConsumption() : 0) + "fe/t ")).withStyle(ChatFormatting.AQUA));
		return true;
	}
}
