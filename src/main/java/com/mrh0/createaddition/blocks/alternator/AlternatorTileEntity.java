package com.mrh0.createaddition.blocks.alternator;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.util.Util;
import com.mrh0.createaddition.transfer.EnergyTransferable;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.utility.Lang;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.List;

@SuppressWarnings({"CommentedOutCode", "UnstableApiUsage"})
public class AlternatorTileEntity extends KineticTileEntity implements EnergyTransferable {
	
	protected final InternalEnergyStorage energy;
	private final LazyOptional<EnergyStorage> lazyEnergy;
	
	private static final long
		MAX_IN = 0,
		MAX_OUT = Config.ALTERNATOR_MAX_OUTPUT.get(),
		CAPACITY = Config.ALTERNATOR_CAPACITY.get(),
		STRESS = Config.BASELINE_STRESS.get();
	private static final double EFFICIENCY = Config.ALTERNATOR_EFFICIENCY.get();

	public AlternatorTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		energy = new InternalEnergyStorage(CAPACITY, MAX_IN, MAX_OUT);
		lazyEnergy = LazyOptional.of(() -> energy);
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		//tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.energy.stored").formatted(TextFormatting.GRAY)));
		//tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + Multimeter.getString(energy) + "fe").formatted(TextFormatting.AQUA)));
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent(CreateAddition.MODID + ".tooltip.energy.production").withStyle(ChatFormatting.GRAY)));
		tooltip.add(new TextComponent(spacing).append(new TextComponent(" " + Util.format(getEnergyProductionRate((int) (isSpeedRequirementFulfilled() ? getSpeed() : 0))) + "fe/t ") // fix
				.withStyle(ChatFormatting.AQUA)).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
		return true;
	}
	
	@Override
	public float calculateStressApplied() {
		float impact = STRESS/256f;
		this.lastStressApplied = impact;
		return impact;
	}

	public boolean isEnergyInput(Direction ignoredSide) {
		return false;
	}

	public boolean isEnergyOutput(Direction side) {
		return side != getBlockState().getValue(AlternatorBlock.FACING);
	}
	
	@Override
	public void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		energy.read(compound);
	}
	
	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		energy.write(compound);
	}
	
	private boolean firstTickState = true;
	
	@Override
	public void tick() {
		super.tick();
		assert level != null;
		if(level.isClientSide())
			return;
		if(firstTickState)
			firstTick();
		firstTickState = false;

		if (Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())
			energy.internalProduceEnergy(getEnergyProductionRate((int) getSpeed()));
		
		//System.out.println(energy.getAmount());
		
		for(Direction d : Direction.values()) {
			if(!isEnergyOutput(d))
				continue;
			/*TileEntity te = world.getTileEntity(pos.offset(d));
			if(te == null)
				continue;
			LazyOptional<IEnergyStorage> opt = te.getStorage(CapabilityEnergy.ENERGY, d.getOpposite());
			IEnergyStorage ies = opt.orElse(null);*/
			EnergyStorage ies = getCachedEnergy(d);
			if(ies == null)
				continue;
			try(Transaction t = Transaction.openOuter()) {
				EnergyStorageUtil.move(energy, ies, MAX_OUT, t);
				t.commit();
			}
			//System.out.println(ext + ":" + getEnergyProductionRate((int)getSpeed()) + ":" + rec + ":" + d);
		}
	}
	
	public static int getEnergyProductionRate(int rpm) {
		rpm = Math.abs(rpm);
		return (int)((double)Config.FE_RPM.get() * ((double)Math.abs(rpm) / 256d) * EFFICIENCY);//return (int)((double)Config.FE_TO_SU.get() * ((double)Math.abs(rpm)/256d) * EFFICIENCY);
	}
	
	@Override
	protected Block getStressConfigKey() {
		return AllBlocks.MECHANICAL_MIXER.get();
	}

	@Override
	public void invalidate() {
        super.invalidate();
		lazyEnergy.invalidate();
	}
	
	public void firstTick() {
		updateCache();
	}

	public void updateCache() {
		assert level != null;
		if(level.isClientSide())
			return;
		for(Direction side : Direction.values()) {
			BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
			if(te == null) {
				setCache(side, LazyOptional.empty());
				continue;
			}
			LazyOptional<EnergyStorage> le = LazyOptional.ofObject(EnergyStorage.SIDED.find(level, worldPosition.relative(side), side.getOpposite()));
			setCache(side, le);
		}
	}
	
	private LazyOptional<EnergyStorage> escacheUp = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheDown = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheNorth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheEast = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheSouth = LazyOptional.empty();
	private LazyOptional<EnergyStorage> escacheWest = LazyOptional.empty();
	
	public void setCache(Direction side, LazyOptional<EnergyStorage> storage) {
		switch (side) {
			case DOWN -> escacheDown = storage;
			case EAST -> escacheEast = storage;
			case NORTH -> escacheNorth = storage;
			case SOUTH -> escacheSouth = storage;
			case UP -> escacheUp = storage;
			case WEST -> escacheWest = storage;
		}
	}
	
	public EnergyStorage getCachedEnergy(Direction side) {
		return switch (side) {
			case DOWN -> escacheDown.orElse(EnergyStorage.EMPTY);
			case EAST -> escacheEast.orElse((EnergyStorage.EMPTY));
			case NORTH -> escacheNorth.orElse((EnergyStorage.EMPTY));
			case SOUTH -> escacheSouth.orElse((EnergyStorage.EMPTY));
			case UP -> escacheUp.orElse((EnergyStorage.EMPTY));
			case WEST -> escacheWest.orElse(EnergyStorage.EMPTY);
		};
	}

	@Nullable
	@Override
	public EnergyStorage getEnergyStorage(@Nullable Direction side) {
		if((isEnergyInput(side) || isEnergyOutput(side)))
			return lazyEnergy.getValueUnsafer();
		return null;
	}
}
