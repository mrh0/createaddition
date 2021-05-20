package com.mrh0.createaddition.blocks.crude_burner;

import java.util.Optional;
import java.util.Random;

import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CrudeBurner extends AbstractFurnaceBlock implements ITE<CrudeBurnerTileEntity> {

	public CrudeBurner(Properties props) {
		super(props);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return CATileEntities.CRUDE_BURNER.create();
	}

	@Override
	protected void interactWith(World world, BlockPos pos, PlayerEntity player) {
		if(world.isRemote())
			return;
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof CrudeBurnerTileEntity) {
			CrudeBurnerTileEntity cbte = (CrudeBurnerTileEntity) tileentity;
			ItemStack held = player.getHeldItemMainhand();
			if(!(held.getItem() instanceof BucketItem))
				return;
			LazyOptional<IFluidHandlerItem> cap = held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
			if(!cap.isPresent())
				return;
			IFluidHandlerItem handler = cap.orElse(null);
			if(handler.getFluidInTank(0).isEmpty())
				return;
			FluidStack stack = handler.getFluidInTank(0);
			Optional<CrudeBurningRecipe> recipe = cbte.find(stack, world);
			if(!recipe.isPresent())
				return;
			
			LazyOptional<IFluidHandler> tecap = cbte.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
			if(!tecap.isPresent())
				return;
			IFluidHandler tehandler = tecap.orElse(null);
			if(tehandler.getTankCapacity(0) - tehandler.getFluidInTank(0).getAmount() < 1000)
				return;
			tehandler.fill(new FluidStack(handler.getFluidInTank(0).getFluid(), 1000), FluidAction.EXECUTE);
			if(!player.isCreative())
				player.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
			player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1f, 1f);
			//FluidStack.EMPTY.getFluid().getFilledBucket();
			/*for(int i = 0; i < handler.getTanks(); i++) {
				FluidStack stack = handler.getFluidInTank(i);
				Optional<CrudeBurningRecipe> recipe = cbte.find(stack, world);
				if(!recipe.isPresent())
					continue;
				
				LazyOptional<IFluidHandler> tecap = cbte.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
				if(!tecap.isPresent())
					continue;
				int fill = tecap.orElse(null).fill(new FluidStack(stack.getFluid(), stack.getAmount()), FluidAction.EXECUTE);
				System.out.println("fill" + fill + ":" + stack.getAmount() + ":" + (held.getItem() instanceof BucketItem));
				handler.drain(fill, FluidAction.EXECUTE);
			}*/
		}
	}

	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
	      if (state.get(LIT)) {
	         double d0 = (double)pos.getX() + 0.5D;
	         double d1 = (double)pos.getY();
	         double d2 = (double)pos.getZ() + 0.5D;
	         if (rand.nextDouble() < 0.1D) {
	            world.playSound(d0, d1, d2, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	         }

	         Direction direction = state.get(FACING);
	         Direction.Axis direction$axis = direction.getAxis();
	         //double d3 = 0.52D;
	         double d4 = rand.nextDouble() * 0.6D - 0.3D;
	         double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
	         double d6 = rand.nextDouble() * 9.0D / 16.0D;
	         double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
	         world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	      }
	   }

	@Override
	public Class<CrudeBurnerTileEntity> getTileEntityClass() {
		return CrudeBurnerTileEntity.class;
	}
}
