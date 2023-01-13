package com.mrh0.createaddition.blocks.modular_accumulator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.mrh0.createaddition.util.Util;
import com.simibubi.create.content.contraptions.fluids.tank.BoilerData;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity;
import com.simibubi.create.content.contraptions.relays.gauge.StressGaugeTileEntity;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;
import com.simibubi.create.content.logistics.block.display.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayLayout;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplaySection;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;

import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModularAccumulatorDisplaySource extends PercentOrProgressBarDisplaySource {

	@Override
	protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
		int mode = getMode(context);
		if (mode == 1)
			return super.formatNumeric(context, currentLevel);
		LangBuilder builder = Lang.number(currentLevel);
		if (context.getTargetTE() instanceof FlapDisplayTileEntity)
			builder.space();
		return builder.translate("generic.unit.stress")
			.component();
	}

	private int getMode(DisplayLinkContext context) {
		return context.sourceConfig()
			.getInt("Mode");
	}

	@Override
	protected Float getProgress(DisplayLinkContext context) {
		if (!(context.getSourceTE()instanceof ModularAccumulatorTileEntity gaugeTile))
			return null;

		float capacity = gaugeTile.energyStorage.getMaxEnergyStored();
		float stored = gaugeTile.energyStorage.getEnergyStored();

		if (capacity == 0)
			return 0f;

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
				.forOptions(Lang.translatedOptions("display_source.kinetic_stress", "progress_bar", "percent",
					"current", "max", "remaining"))
				.titled(Lang.translateDirect("display_source.kinetic_stress.display")),
			"Mode");
	}

	@Override
	protected String getTranslationKey() {
		return "kinetic_stress";
	}

	/*
	public static final List<MutableComponent> notEnoughSpaceSingle =
		List.of(Lang.translateDirect("display_source.boiler.not_enough_space")
			.append(Lang.translateDirect("display_source.boiler.for_boiler_status")));

	public static final List<MutableComponent> notEnoughSpaceDouble =
		List.of(Lang.translateDirect("display_source.boiler.not_enough_space"),
			Lang.translateDirect("display_source.boiler.for_boiler_status"));

	public static final List<List<MutableComponent>> notEnoughSpaceFlap =
		List.of(List.of(Lang.translateDirect("display_source.boiler.not_enough_space")),
			List.of(Lang.translateDirect("display_source.boiler.for_boiler_status")));

	@Override
	public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
		if (stats.maxRows() < 2)
			return notEnoughSpaceSingle;
		else if (stats.maxRows() < 3)
			return notEnoughSpaceDouble;

		boolean isBook = context.getTargetTE() instanceof LecternBlockEntity;

		if (isBook) {
			Stream<MutableComponent> componentList = getComponents(context, false).map(components -> {
				Optional<MutableComponent> reduce = components.stream()
					.reduce(MutableComponent::append);
				return reduce.orElse(EMPTY_LINE);
			});

			return List.of(componentList.reduce((comp1, comp2) -> comp1.append(Components.literal("\n"))
				.append(comp2))
				.orElse(EMPTY_LINE));
		}

		return getComponents(context, false).map(components -> {
			Optional<MutableComponent> reduce = components.stream()
				.reduce(MutableComponent::append);
			return reduce.orElse(EMPTY_LINE);
		})
			.toList();
	}

	@Override
	public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
		if (stats.maxRows() < 4) {
			context.flapDisplayContext = Boolean.FALSE;
			return notEnoughSpaceFlap;
		}

		List<List<MutableComponent>> components = getComponents(context, true).toList();

		if (stats.maxColumns() * FlapDisplaySection.MONOSPACE < 6 * FlapDisplaySection.MONOSPACE + components.get(1)
			.get(1)
			.getString()
			.length() * FlapDisplaySection.WIDE_MONOSPACE) {
			context.flapDisplayContext = Boolean.FALSE;
			return notEnoughSpaceFlap;
		}

		return components;
	}

	@Override
	public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayTileEntity flapDisplay,
		FlapDisplayLayout layout, int lineIndex) {
		if (lineIndex == 0 || context.flapDisplayContext instanceof Boolean b && !b) {
			if (layout.isLayout("Default"))
				return;

			layout.loadDefault(flapDisplay.getMaxCharCount());
			return;
		}

		String layoutKey = "Accumulator";
		if (layout.isLayout(layoutKey))
			return;

		int labelLength = (int) (labelWidth() * FlapDisplaySection.MONOSPACE);
		FlapDisplaySection label = new FlapDisplaySection(labelLength, "alphabet", false, true);

		layout.configure(layoutKey, List.of(label));
	}

	private Stream<List<MutableComponent>> getComponents(DisplayLinkContext context, boolean forFlapDisplay) {
		BlockEntity sourceTE = context.getSourceTE();
		if (!(sourceTE instanceof ModularAccumulatorTileEntity te))
			return Stream.of(EMPTY);

		te = te.getControllerTE();
		if (te == null)
			return Stream.of(EMPTY);
		int energyStored = te.energyStorage.getEnergyStored();
		int energyCapacity = te.energyStorage.getEnergyStored();
		
		MutableComponent stored = labelOf(forFlapDisplay ? "stored" : "");
		MutableComponent capacity = labelOf(forFlapDisplay ? "capacity" : "");

		int lw = labelWidth();
		if (forFlapDisplay) {
			stored = Components.literal(Strings.repeat(' ', lw - labelWidthOf("stored"))).append(stored).append(" ").append(Util.getTextComponent(energyStored, "fe"));
			capacity = Components.literal(Strings.repeat(' ', lw - labelWidthOf("capacity"))).append(capacity).append(" ").append(Util.getTextComponent(energyCapacity, "fe"));
		}

		return Stream.of(List.of(labelOf("title")),
			List.of(stored),
			List.of(capacity));
	}

	private int labelWidth() {
		return Math.max(labelWidthOf("stored"), labelWidthOf("capacity"));
	}

	private int labelWidthOf(String label) {
		return labelOf(label).getString().length();
	}

	private MutableComponent labelOf(String label) {
		if (label.isBlank())
			return Components.empty();
		return Component.translatable("createaddition.modular_accumulator.display_source." + label);
	}

	@Override
	protected String getTranslationKey() {
		return "accumulator_status";
	}
	*/
}
