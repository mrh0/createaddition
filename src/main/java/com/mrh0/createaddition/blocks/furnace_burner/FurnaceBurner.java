package com.mrh0.createaddition.blocks.furnace_burner;

import java.util.Random;

import javax.annotation.Nullable;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlock;
import com.mrh0.createaddition.blocks.base.AbstractBurnerBlockEntity;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FurnaceBurner extends AbstractBurnerBlock implements ITE<FurnaceBurnerTileEntity> {

	public FurnaceBurner(Properties props) {
		super(props);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.FURNACE_BURNER.create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (world.isClientSide())
			return InteractionResult.SUCCESS;
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof FurnaceBurnerTileEntity) {
			FurnaceBurnerTileEntity fbte = (FurnaceBurnerTileEntity) tileentity;
			ItemStack currentStack = fbte.getItem(FurnaceBurnerTileEntity.SLOT_FUEL);
			if (player.isShiftKeyDown()) {
				if (currentStack.getCount() > 0) {
					Containers.dropItemStack(world, player.position().x, player.position().y, player.position().z,
							currentStack.copy());
					fbte.removeItemNoUpdate(FurnaceBurnerTileEntity.SLOT_FUEL);
				}
				return InteractionResult.CONSUME;
			}

			ItemStack heald = player.getMainHandItem();
			if (!fbte.canPlaceItem(FurnaceBurnerTileEntity.SLOT_FUEL, heald))
				return InteractionResult.CONSUME;

			if (currentStack.isEmpty()) {
				fbte.setItem(FurnaceBurnerTileEntity.SLOT_FUEL, heald.copy());
				heald.setCount(0);
				return InteractionResult.CONSUME;
			}

			if (heald.getItem() != currentStack.getItem())
				return InteractionResult.CONSUME;

			ItemStack newStack = new ItemStack(currentStack.getItem(),
					Math.min(currentStack.getCount() + heald.getCount(), currentStack.getMaxStackSize()));
			heald.setCount(Util.getMergeRest(heald, currentStack));

			fbte.setItem(FurnaceBurnerTileEntity.SLOT_FUEL, newStack);
		}
		return InteractionResult.CONSUME;
	}

	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
		if (!world.isClientSide())
			return;
		if (state.getValue(LIT)) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D)
				world.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F,
						false);

			Direction direction = state.getValue(FACING);
			Direction.Axis direction$axis = direction.getAxis();
			// double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
			double d6 = rand.nextDouble() * 6.0D / 16.0D;
			double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
			world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Class<FurnaceBurnerTileEntity> getTileEntityClass() {
		return FurnaceBurnerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends FurnaceBurnerTileEntity> getTileEntityType() {
		return CATileEntities.FURNACE_BURNER.get();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_,
			BlockEntityType<T> p_153214_) {
		return createBurnerTicker(p_153212_, p_153214_, CATileEntities.FURNACE_BURNER.get());
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
		return p_152134_ == p_152133_ ? (BlockEntityTicker<A>) p_152135_ : null;
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createBurnerTicker(Level p_151988_,
			BlockEntityType<T> p_151989_, BlockEntityType<? extends AbstractBurnerBlockEntity> p_151990_) {
		return p_151988_.isClientSide ? null
				: createTickerHelper(p_151989_, p_151990_, FurnaceBurnerTileEntity::serverTick);
	}
}
