package com.mrh0.createaddition.compat.forge;
/*
import java.util.List;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours;
import com.simibubi.create.content.logistics.block.display.DisplayBehaviour;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyDisplaySource extends PercentOrProgressBarDisplaySource {
	public final static ForgeEnergyDisplaySource INSTANCE = new ForgeEnergyDisplaySource();
	//private final static DisplayBehaviour BEHAVIOUR = AllDisplayBehaviours.register(CreateAddition.asResource(INSTANCE.getTranslationKey()), INSTANCE);

	@Override
	protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
		int mode = getMode(context);
		if (mode == 1)
			return super.formatNumeric(context, currentLevel);
		return Util.getTextComponent(Math.round(currentLevel), "fe");
	}

	private int getMode(DisplayLinkContext context) {
		return context.sourceConfig()
			.getInt("Mode");
	}

	@Override
	protected Float getProgress(DisplayLinkContext context) {
		BlockEntity be = context.getSourceTE();
		if (be == null) return null;
		LazyOptional<IEnergyStorage> cap = be.getCapability(ForgeCapabilities.ENERGY).cast();
		if(!cap.isPresent()) return null;
		IEnergyStorage es = cap.orElse(null);
		if(es == null) return null;
		
		float capacity = es.getMaxEnergyStored();
		float stored = es.getEnergyStored();

		if (capacity == 0) return 0f;

		return switch (getMode(context)) {
			case 0, 1 -> stored / capacity;
			case 2 -> stored;
			case 3 -> capacity;
			case 4 -> capacity - stored;
			default -> 0f;
		};
	}

	@Override
	protected boolean allowsLabeling(DisplayLinkContext context) {
		return true;
	}

	@Override
	protected boolean progressBarActive(DisplayLinkContext context) {
		return getMode(context) == 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
		boolean isFirstLine) {
		super.initConfigurationWidgets(context, builder, isFirstLine);
		if (isFirstLine)
			return;
		builder.addSelectionScrollInput(0, 120,
			(si, l) -> si
				.forOptions(List.of(
							Component.translatable("createaddition.display_source.accumulator.progress_bar"),
							Component.translatable("createaddition.display_source.accumulator.percent"),
							Component.translatable("createaddition.display_source.accumulator.current"),
							Component.translatable("createaddition.display_source.accumulator.max"),
							Component.translatable("createaddition.display_source.accumulator.remaining")
						)) // Lang.translatedOptions("display_source.kinetic_stress", "progress_bar", "percent", "current", "max", "remaining")
				.titled(Component.translatable("createaddition.display_source.accumulator.display")), "Mode");
	}

	@Override
	protected String getTranslationKey() {
		return "forge_energy";
	}
	
	public static void register() {}
}*/