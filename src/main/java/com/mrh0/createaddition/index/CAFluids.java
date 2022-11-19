package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;

import static net.minecraft.world.item.Items.BUCKET;

@SuppressWarnings("UnstableApiUsage")
public class CAFluids {
	public static void register() {}
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.creativeModeTab(() -> ModGroup.MAIN);

	public static final FluidEntry<SimpleFlowableFluid.Flowing> BIOETHANOL;
	public static final FluidEntry<SimpleFlowableFluid.Flowing> SEED_OIL;
	public static final FluidStack BIOETHANOL_FLUID_STACK;
	public static final FluidStack SEED_OIL_FLUID_STACK;

	static  {
		BIOETHANOL = REGISTRATE
				.fluid("bioethanol",
						new ResourceLocation(CreateAddition.MODID, "fluid/bioethanol_still"),
						new ResourceLocation(CreateAddition.MODID,"fluid/bioethanol_flow")
				)
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.flowSpeed(6)
						.blastResistance(100f))
				.fluidAttributes(() -> new CreateAdditionsAttributeHandler("fluid.bioethanol", 2000, 1400))
				.lang("Bioethanol")
				.onRegisterAfter(Registry.ITEM_REGISTRY, fluid -> {
					Fluid source = fluid.getSource();
					FluidStorage.combinedItemApiProvider(source.getBucket()).register(context ->
							new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(source), FluidConstants.BUCKET));
					FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
							new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(source.getBucket()), source, FluidConstants.BUCKET));
				}).register();

		SEED_OIL = REGISTRATE
				.fluid("seed_oil",
						new ResourceLocation(CreateAddition.MODID, "fluid/seed_oil_still"),
						new ResourceLocation(CreateAddition.MODID, "fluid/seed_oil_flow")
				)
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.flowSpeed(6)
						.blastResistance(100f))
				.fluidAttributes(() -> new CreateAdditionsAttributeHandler("fluid.seed_oil", 2500, 1600))
				.lang("Seed Oil")
				.onRegisterAfter(Registry.ITEM_REGISTRY, fluid -> {
					Fluid source = fluid.getSource();
					FluidStorage.combinedItemApiProvider(source.getBucket()).register(context ->
							new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(source), FluidConstants.BUCKET));
					FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
							new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(source.getBucket()), source, FluidConstants.BUCKET));
				}).register();

		BIOETHANOL_FLUID_STACK = new FluidStack(BIOETHANOL.getSource().getSource(), 81000);
		SEED_OIL_FLUID_STACK = new FluidStack(BIOETHANOL.getSource().getSource(), 81000);
	}


	private record CreateAdditionsAttributeHandler(Component name, int viscosity, boolean lighterThanAir) implements FluidVariantAttributeHandler {
		private CreateAdditionsAttributeHandler(String key, int viscosity, int density) {
			this(Component.translatable(key), viscosity, density <= 0);
		}

		@Override
		public Component getName(FluidVariant fluidVariant) {
			return name;
		}

		@Override
		public int getViscosity(FluidVariant variant, @Nullable Level world) {
			return viscosity;
		}

		@Override
		public boolean isLighterThanAir(FluidVariant variant) {
			return lighterThanAir;
		}
	}
}
