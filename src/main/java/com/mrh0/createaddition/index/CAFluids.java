package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.FluidEntry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CAFluids {
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.creativeModeTab(() -> ModGroup.MAIN);
	
	public static final FluidEntry<ForgeFlowingFluid.Flowing> SEED_OIL =
			REGISTRATE.fluid("seed_oil", new ResourceLocation("createaddition","fluid/seed_oil_still"), new ResourceLocation("createaddition","fluid/seed_oil_flow"),
					NoColorFluidAttributes::new)//.standardFluid("seed_oil", NoColorFluidAttributes::new)
					.attributes(b -> b.viscosity(2000)
							.density(1400))
					.properties(p -> p.levelDecreasePerBlock(2)
							.tickRate(15)
							.slopeFindDistance(6)
							.explosionResistance(100f))
					.source(ForgeFlowingFluid.Source::new)
					.bucket()
					.properties(p -> p.stacksTo(1))
					.build()
					.register();
	
	public static final FluidEntry<ForgeFlowingFluid.Flowing> BIOETHANOL =
			REGISTRATE.fluid("bioethanol", new ResourceLocation("createaddition","fluid/bioethanol_still"), new ResourceLocation("createaddition","fluid/bioethanol_flow"),
					NoColorFluidAttributes::new)
					.attributes(b -> b.viscosity(2500)
							.density(1600))
					.properties(p -> p.levelDecreasePerBlock(2)
							.tickRate(15)
							.slopeFindDistance(6)
							.explosionResistance(100f))
					.source(ForgeFlowingFluid.Source::new)
					.bucket()
					.properties(p -> p.stacksTo(1))
					.build()
					.register();
	
	private static class NoColorFluidAttributes extends FluidAttributes {
		protected NoColorFluidAttributes(Builder builder, Fluid fluid) {
			super(builder, fluid);
		}

		@Override
		public int getColor(BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}
	}
	
	public static void register() {}
}
