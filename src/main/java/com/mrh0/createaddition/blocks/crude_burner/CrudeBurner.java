package com.mrh0.createaddition.blocks.crude_burner;

import com.mrh0.createaddition.blocks.base.AbstractBurnerBlock;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.recipe.crude_burning.CrudeBurningRecipe;
import com.simibubi.create.foundation.block.ITE;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class CrudeBurner extends AbstractBurnerBlock implements ITE<CrudeBurnerTileEntity> {

	public CrudeBurner(Properties props) {
		super(props);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CATileEntities.CRUDE_BURNER.create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (world.isClientSide())
			return InteractionResult.SUCCESS;
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof CrudeBurnerTileEntity) {
			CrudeBurnerTileEntity cbte = (CrudeBurnerTileEntity) tileentity;
			ItemStack held = player.getMainHandItem();
			if (!(held.getItem() instanceof BucketItem))
				return InteractionResult.CONSUME;
			Storage<FluidVariant> handler = ContainerItemContext.withInitial(held).find(FluidStorage.ITEM);
			if (handler == null)
				return InteractionResult.CONSUME;
			if (TransferUtil.getFirstFluid(handler).isEmpty())
				return InteractionResult.CONSUME;
			FluidStack stack = TransferUtil.getFirstFluid(handler);
			Optional<CrudeBurningRecipe> recipe = cbte.find(stack, world);
			if (!recipe.isPresent())
				return InteractionResult.CONSUME;

			Storage<FluidVariant> tehandler = TransferUtil.getFluidStorage(cbte);
			if (tehandler == null)
				return InteractionResult.CONSUME;
			try(Transaction t = TransferUtil.getTransaction()) {
				for (StorageView<FluidVariant> view : tehandler.iterable(t)) {
					if (view.getCapacity() - view.getAmount() < FluidConstants.BUCKET) {
						t.abort();
						return InteractionResult.CONSUME;
					}
					System.out.println(view.getResource().getFluid());
					tehandler.insert(view.getResource(), FluidConstants.BUCKET, t);
					t.commit();
					break;
				}


			}
			if (!player.isCreative())
				player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
			player.playSound(SoundEvents.BUCKET_EMPTY, 1f, 1f);
		}
		return InteractionResult.CONSUME;
	}

	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
		if (state.getValue(LIT)) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D) {
				world.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F,
						false);
			}

			Direction direction = state.getValue(FACING);
			Direction.Axis direction$axis = direction.getAxis();
			// double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
			double d6 = rand.nextDouble() * 9.0D / 16.0D;
			double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
			world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public Class<CrudeBurnerTileEntity> getTileEntityClass() {
		return CrudeBurnerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends CrudeBurnerTileEntity> getTileEntityType() {
		return CATileEntities.CRUDE_BURNER.get();
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
		return p_152134_ == p_152133_ ? (BlockEntityTicker<A>) p_152135_ : null;
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createBurnerTicker(Level level,
			BlockEntityType<T> type1, BlockEntityType<? extends CrudeBurnerTileEntity> type2) {
		return level.isClientSide ? null : createTickerHelper(type1, type2, CrudeBurnerTileEntity::crudeServerTick);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return createBurnerTicker(level, type, CATileEntities.CRUDE_BURNER.get());
	}
}
