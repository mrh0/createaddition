package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.AllFluids;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CAFluids {
	static {
		CreateAddition.REGISTRATE.setCreativeTab(CACreativeModeTabs.MAIN_TAB);
	}

	public static FluidEntry<ForgeFlowingFluid.Flowing> SEED_OIL;
	public static FluidEntry<ForgeFlowingFluid.Flowing> BIOETHANOL;


	/**
	 * (From Create)
	 * Removing alpha from tint prevents optifine from forcibly applying biome
	 * colors to modded fluids (Makes translucent fluids disappear)
	 */
	private static class NoColorFluidAttributes extends AllFluids.TintedFluidType {

		public NoColorFluidAttributes(Properties properties, ResourceLocation stillTexture,
			ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return NO_TINT;
		}

		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}

	}

	public static void register() {
		var seedOil = CreateAddition.REGISTRATE.fluid("seed_oil", new ResourceLocation("createaddition","fluid/seed_oil_still"), new ResourceLocation("createaddition","fluid/seed_oil_flow"),
				NoColorFluidAttributes::new)//.standardFluid("seed_oil", NoColorFluidAttributes::new)
				.properties(b -> b.viscosity(2000)
						.density(1400))
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.slopeFindDistance(6)
						.explosionResistance(100f))
				.source(ForgeFlowingFluid.Source::new);

		var seedOilBucket = seedOil.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		SEED_OIL = seedOil.register();

		var bioethanol = CreateAddition.REGISTRATE.fluid("bioethanol", new ResourceLocation("createaddition","fluid/bioethanol_still"), new ResourceLocation("createaddition","fluid/bioethanol_flow"),
				NoColorFluidAttributes::new)
				.properties(b -> b.viscosity(2500)
						.density(1600))
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.slopeFindDistance(6)
						.explosionResistance(100f))
				.source(ForgeFlowingFluid.Source::new);
		var bioethanolBucket = bioethanol.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		BIOETHANOL = bioethanol.register();

		//REGISTRATE.addToSection(seedOilBucket, AllSections.MATERIALS);
		//REGISTRATE.addToSection(bioethanolBucket, AllSections.MATERIALS);
	}
}
