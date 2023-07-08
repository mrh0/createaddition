package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CAFluids {
	static {
		CreateAddition.REGISTRATE.creativeModeTab(() -> ModGroup.MAIN);
	}

	public static FluidEntry<SimpleFlowableFluid.Flowing> SEED_OIL;
	public static FluidEntry<SimpleFlowableFluid.Flowing> BIOETHANOL;
	
	public static void register() {
		var seedOil = CreateAddition.REGISTRATE.fluid("seed_oil", new ResourceLocation("createaddition","fluid/seed_oil_still"), new ResourceLocation("createaddition","fluid/seed_oil_flow"))//.standardFluid("seed_oil", NoColorFluidAttributes::new)
				.attributes(b -> new CreateAdditionsAttributeHandler("fluid.createaddition.bioethanol", 2000, 1400))
				.properties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.flowSpeed(6)
						.blastResistance(100f))
				.source(SimpleFlowableFluid.Still::new);
		
		var seedOilBucket = seedOil.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		SEED_OIL = seedOil.register();
		
		var bioethanol = CreateAddition.REGISTRATE.fluid("bioethanol", new ResourceLocation("createaddition","fluid/bioethanol_still"), new ResourceLocation("createaddition","fluid/bioethanol_flow"))
				.attributes(b -> new CreateAdditionsAttributeHandler("fluid.createaddition.seed_oil", 2500, 1600))
				.properties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.flowSpeed(6)
						.blastResistance(100f))
				.source(SimpleFlowableFluid.Still::new);
		var bioethanolBucket = bioethanol.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		BIOETHANOL = bioethanol.register();
		
		//REGISTRATE.addToSection(seedOilBucket, AllSections.MATERIALS);
		//REGISTRATE.addToSection(bioethanolBucket, AllSections.MATERIALS);
	}

	private record CreateAdditionsAttributeHandler(Component name, int viscosity, boolean lighterThanAir) implements FluidVariantAttributeHandler {
		private CreateAdditionsAttributeHandler(String key, int viscosity, int density) {
			this(new TranslatableComponent(key), viscosity, density <= 0);
		}

		@Override
		public Component getName(FluidVariant fluidVariant) {
			return name.copy();
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
