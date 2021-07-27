package com.mrh0.createaddition.blocks.treated_gearbox;

import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.Config;
import com.mrh0.createaddition.energy.InternalEnergyStorage;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.item.Multimeter;
import blusunrize.immersiveengineering.api.energy.IRotationAcceptor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour.StepContext;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class TreatedGearboxTileEntity extends GeneratingKineticTileEntity implements IRotationAcceptor {
	
	private float rotation = 0;
	private int got_rotation = 0;
	
	private static final Integer
		CONVERSION_RATE = 2,
		STRESS = Config.BASELINE_STRESS.get();
	
	private boolean active = false;

	public TreatedGearboxTileEntity(TileEntityType<? extends TreatedGearboxTileEntity> type) {
		super(type);
		setLazyTickRate(20);
	}

	@Override
	public void inputRotation(double rotation, @Nonnull Direction side)
	{
		if(side!=getBlockState().getValue(TreatedGearboxBlock.FACING))
			return;
		this.got_rotation = 5;
		if (Math.abs(this.rotation - rotation) > 0.05) {
			this.rotation = (float) rotation;
			// System.out.println("New rotation!\n");
			System.out.println(this.rotation);
			updateGeneratedRotation();
		}
	}
	
	public float calculateAddedStressCapacity() {
		float capacity = STRESS/256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}
	
	@Override
	public boolean addToGoggleTooltip(List<ITextComponent> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		tooltip.add(new StringTextComponent(spacing).append(new TranslationTextComponent(CreateAddition.MODID + ".tooltip.ie_rotation.consumption").withStyle(TextFormatting.GRAY)));
		tooltip.add(new StringTextComponent(spacing).append(new StringTextComponent(" " + this.rotation + "_whatever ")
				.withStyle(TextFormatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").withStyle(TextFormatting.DARK_GRAY)));
		added = true;
		return added;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CABlocks.TREATED_GEARBOX.has(getBlockState()))
			return 0;
		return (float) Math.floor(rotation) * CONVERSION_RATE;
	}
	
	@Override
	protected Block getStressConfigKey() {
		return AllBlocks.WATER_WHEEL.get();
	}
	
	
	@Override
	public void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		active = compound.getBoolean("active");
		rotation = compound.getFloat("rotation");
	}
	
	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		compound.putBoolean("active", active);
		compound.putFloat("rotation", rotation);
		super.write(compound, clientPacket);
	}


	@Override
	public void tick() {
		super.tick();

		if(level.isClientSide())
			return;
		
		if (this.got_rotation > 0) {
			this.got_rotation--;
		}
		else if (this.rotation != 0) {
			this.rotation = 0;
			// System.out.println("Rotation stopped!");
			updateGeneratedRotation();
		}
	}

	@Override
	public World getWorld() {
		return getLevel();
	}
}
