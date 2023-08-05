package com.mrh0.createaddition.blocks.modular_accumulator;

import com.mrh0.createaddition.index.CABlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;

public class ModularAccumulatorBlock extends Block implements IWrenchable, IBE<ModularAccumulatorBlockEntity> {

	public static final BooleanProperty TOP = BooleanProperty.create("top");
	public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");

	private boolean creative;

	public static ModularAccumulatorBlock regular(Properties props) {
		return new ModularAccumulatorBlock(props, false);
	}

	public static ModularAccumulatorBlock creative(Properties props) {
		return new ModularAccumulatorBlock(props, true);
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		// AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
	}

	public static boolean isAccumulator(BlockState state) {
		return state.getBlock() instanceof ModularAccumulatorBlock;
	}

	protected ModularAccumulatorBlock(Properties p_i48440_1_, boolean creative) {
		super(p_i48440_1_);
		this.creative = creative;
		registerDefaultState(defaultBlockState().setValue(TOP, true)
			.setValue(BOTTOM, true));
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() == state.getBlock())
			return;
		if (moved)
			return;
		withBlockEntityDo(world, pos, ModularAccumulatorBlockEntity::updateConnectivity);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(TOP, BOTTOM);
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		//withTileEntityDo(context.getLevel(), context.getClickedPos(), ModularAccumulatorTileEntity::toggleWindows);
		return InteractionResult.SUCCESS;
	}

	static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0, 4, 0, 16, 16, 16);

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
		CollisionContext pContext) {
		if (pContext == CollisionContext.empty())
			return CAMPFIRE_SMOKE_CLIP;
		return pState.getShape(pLevel, pPos);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return Shapes.block();
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
		LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		//if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this)
		//	withTileEntityDo(pLevel, pCurrentPos, ModularAccumulatorTileEntity::updateBoilerTemperature);
		return pState;
	}

	/*@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
		BlockHitResult ray) {
		ItemStack heldItem = player.getItemInHand(hand);
		boolean onClient = world.isClientSide;

		if (heldItem.isEmpty())
			return InteractionResult.PASS;

		FluidExchange exchange = null;
		ModularAccumulatorTileEntity te = ConnectivityHandler.partAt(getTileEntityType(), world, pos);
		if (te == null)
			return InteractionResult.FAIL;

		LazyOptional<IFluidHandler> tankCapability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
		if (!tankCapability.isPresent())
			return InteractionResult.PASS;
		IFluidHandler fluidTank = tankCapability.orElse(null);
		FluidStack prevFluidInTank = fluidTank.getFluidInTank(0)
			.copy();

		if (FluidHelper.tryEmptyItemIntoTE(world, player, hand, heldItem, te))
			exchange = FluidExchange.ITEM_TO_TANK;
		else if (FluidHelper.tryFillItemFromTE(world, player, hand, heldItem, te))
			exchange = FluidExchange.TANK_TO_ITEM;

		if (exchange == null) {
			if (EmptyingByBasin.canItemBeEmptied(world, heldItem)
				|| GenericItemFilling.canItemBeFilled(world, heldItem))
				return InteractionResult.SUCCESS;
			return InteractionResult.PASS;
		}

		SoundEvent soundevent = null;
		BlockState fluidState = null;
		FluidStack fluidInTank = tankCapability.map(fh -> fh.getFluidInTank(0))
			.orElse(FluidStack.EMPTY);

		if (exchange == FluidExchange.ITEM_TO_TANK) {
			if (creative && !onClient) {
				FluidStack fluidInItem = EmptyingByBasin.emptyItem(world, heldItem, true)
					.getFirst();
				if (!fluidInItem.isEmpty() && fluidTank instanceof CreativeSmartFluidTank)
					((CreativeSmartFluidTank) fluidTank).setContainedFluid(fluidInItem);
			}

			Fluid fluid = fluidInTank.getFluid();
			fluidState = fluid.defaultFluidState()
				.createLegacyBlock();
			soundevent = FluidHelper.getEmptySound(fluidInTank);
		}

		if (exchange == FluidExchange.TANK_TO_ITEM) {
			if (creative && !onClient)
				if (fluidTank instanceof CreativeSmartFluidTank)
					((CreativeSmartFluidTank) fluidTank).setContainedFluid(FluidStack.EMPTY);

			Fluid fluid = prevFluidInTank.getFluid();
			fluidState = fluid.defaultFluidState()
				.createLegacyBlock();
			soundevent = FluidHelper.getFillSound(prevFluidInTank);
		}

		if (soundevent != null && !onClient) {
			float pitch = Mth
				.clamp(1 - (1f * fluidInTank.getAmount() / (ModularAccumulatorTileEntity.getCapacityMultiplier() * 16)), 0, 1);
			pitch /= 1.5f;
			pitch += .5f;
			pitch += (world.random.nextFloat() - .5f) / 4f;
			world.playSound(null, pos, soundevent, SoundSource.BLOCKS, .5f, pitch);
		}

		if (!fluidInTank.isFluidStackIdentical(prevFluidInTank)) {
			if (te instanceof ModularAccumulatorTileEntity) {
				ModularAccumulatorTileEntity controllerTE = ((ModularAccumulatorTileEntity) te).getControllerTE();
				if (controllerTE != null) {
					if (fluidState != null && onClient) {
						BlockParticleOption blockParticleData =
							new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
						float level = (float) fluidInTank.getAmount() / fluidTank.getTankCapacity(0);

						boolean reversed = fluidInTank.getFluid()
							.getFluidType()
							.isLighterThanAir();
						if (reversed)
							level = 1 - level;

						Vec3 vec = ray.getLocation();
						vec = new Vec3(vec.x, controllerTE.getBlockPos()
							.getY() + level * (controllerTE.height - .5f) + .25f, vec.z);
						Vec3 motion = player.position()
							.subtract(vec)
							.scale(1 / 20f);
						vec = vec.add(motion);
						world.addParticle(blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
						return InteractionResult.SUCCESS;
					}

					controllerTE.sendDataImmediately();
					controllerTE.setChanged();
				}
			}
		}

		return InteractionResult.SUCCESS;
	}*/

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
			BlockEntity te = world.getBlockEntity(pos);
			if (!(te instanceof ModularAccumulatorBlockEntity))
				return;
			ModularAccumulatorBlockEntity accumulatorTE = (ModularAccumulatorBlockEntity) te;
			world.removeBlockEntity(pos);
			CAConnectivityHandler.splitMulti(accumulatorTE);
		}
	}

	// Blocks are less noisy when placed in batch
	public static final SoundType SILENCED_METAL =
		new ForgeSoundType(0.1F, 1.5F, () -> SoundEvents.METAL_BREAK, () -> SoundEvents.METAL_STEP,
			() -> SoundEvents.METAL_PLACE, () -> SoundEvents.METAL_HIT, () -> SoundEvents.METAL_FALL);

	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
		SoundType soundType = super.getSoundType(state, world, pos, entity);
		if (entity != null && entity.getPersistentData()
			.contains("SilenceTankSound"))
			return SILENCED_METAL;
		return soundType;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return getBlockEntityOptional(worldIn, pos).map(ModularAccumulatorBlockEntity::getControllerBE)
			.map(te -> ComparatorUtil.fractionToRedstoneLevel(te.getFillState()))
			.orElse(0);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		if(tileentity != null) {
			if(tileentity instanceof ModularAccumulatorBlockEntity) {
				((ModularAccumulatorBlockEntity)tileentity).updateCache();
			}
		}
	}

	@Override
	public Class<ModularAccumulatorBlockEntity> getBlockEntityClass() {
		return ModularAccumulatorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ModularAccumulatorBlockEntity> getBlockEntityType() {
		return CABlockEntities.MODULAR_ACCUMULATOR.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CABlockEntities.MODULAR_ACCUMULATOR.create(pos, state);
	}
}
