package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.groups.ModGroup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.entry.RegistryEntry;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CAFluids {
	private static final CreateRegistrate REGISTRATE = CreateAddition.registrate()
			.itemGroup(() -> ModGroup.MAIN);
	
	public static RegistryEntry<ForgeFlowingFluid.Flowing> SEED_OIL =
		REGISTRATE.fluid("seed_oil", new ResourceLocation("createaddition:fluid/seed_oil_still"), new ResourceLocation("createaddition:fluid/seed_oil_flow"), NoColorFluidAttributes::new)
			.attributes(b -> b.viscosity(1000)
				.density(1400))
			.properties(p -> p.levelDecreasePerBlock(2)
				.tickRate(15)
				.slopeFindDistance(6)
				.explosionResistance(100f))
			.bucket()
			.properties(p -> p.maxStackSize(1))
			.build()
			.register();
	
	private static class NoColorFluidAttributes extends FluidAttributes {
		protected NoColorFluidAttributes(Builder builder, Fluid fluid) {
			super(builder, fluid);
		}

		@Override
		public int getColor(IBlockDisplayReader world, BlockPos pos) {
			return 0x00ffffff;
		}
	}
	
	public static void register() {}
}
