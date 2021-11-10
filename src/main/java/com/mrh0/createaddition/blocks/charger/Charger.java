package com.mrh0.createaddition.blocks.charger;

import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.block.depot.SharedDepotBlockMethods;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.DirectBeltInputBehaviour;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.block.AbstractBlock.Properties;

public class Charger extends Block implements ITE<ChargerTileEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public static final VoxelShape CHARGER_SHAPE = VoxelShapes.or(Block.box(0, 0, 0, 16, 11, 16), Block.box(1, 1, 1, 15, 13, 15));
	
	public Charger(Properties prop) {
		super(prop);
	}

	@Override
	public Class<ChargerTileEntity> getTileEntityClass() {
		return ChargerTileEntity.class;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return CATileEntities.CHARGER.create();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return CHARGER_SHAPE;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return defaultBlockState().setValue(FACING, c.getPlayer().isShiftKeyDown() ? c.getHorizontalDirection().getCounterClockWise() : c.getHorizontalDirection().getClockWise());
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(worldIn.isClientSide())
			return ActionResultType.SUCCESS;
		TileEntity te = worldIn.getBlockEntity(pos);
		if(te != null) {
			if(te instanceof ChargerTileEntity) {
				ChargerTileEntity cte = (ChargerTileEntity) te;
				ItemStack held = player.getItemInHand(handIn);
				if(cte.hasChargedStack()) {
					InventoryHelper.dropItemStack(worldIn, (double)player.position().x(), (double)player.position().y(), (double)player.position().z(), cte.getChargedStack());
					//player.setHeldItem(handIn, cte.getChargedStack());
					cte.setChargedStack(ItemStack.EMPTY);
					worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					return ActionResultType.CONSUME;
				}
				
				if(!held.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
					if(held.getItem() != CAItems.CHARGING_CHROMATIC_COMPOUND.get() && held.getItem() != AllItems.CHROMATIC_COMPOUND.get())
						return ActionResultType.PASS;
				}
				cte.setChargedStack(held);
				//if(!player.isCreative())
				held.shrink(1);
				worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				return ActionResultType.CONSUME;
			}
		}
		return ActionResultType.PASS;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if(te != null) {
			if(te instanceof ChargerTileEntity) {
				ChargerTileEntity sste = (ChargerTileEntity) te;
				InventoryHelper.dropItemStack(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), sste.getChargedStack());
			}
			te.setRemoved();
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}
	
	
	// Depot
	/*@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockRayTraceResult ray) {
		if (ray.getDirection() != Direction.UP)
			return ActionResultType.PASS;
		if (world.isClientSide)
			return ActionResultType.SUCCESS;

		ChargerBehaviour behaviour = get(world, pos);
		if (behaviour == null)
			return ActionResultType.PASS;
		if (!behaviour.canAcceptItems.get())
			return ActionResultType.SUCCESS;

		ItemStack heldItem = player.getItemInHand(hand);
		boolean wasEmptyHanded = heldItem.isEmpty();
		boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

		ItemStack mainItemStack = behaviour.getHeldItemStack();
		if (!mainItemStack.isEmpty()) {
			player.inventory.placeItemBackInInventory(world, mainItemStack);
			behaviour.removeHeldItem();
			world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, .2f,
					1f + Create.RANDOM.nextFloat());
		}
		ItemStackHandler outputs = behaviour.processingOutputBuffer;
		for (int i = 0; i < outputs.getSlots(); i++)
			player.inventory.placeItemBackInInventory(world, outputs.extractItem(i, 64, false));

		if (!wasEmptyHanded && !shouldntPlaceItem) {
			TransportedItemStack transported = new TransportedItemStack(heldItem);
			transported.insertedFrom = player.getDirection();
			transported.prevBeltPosition = .25f;
			transported.beltPosition = .25f;
			behaviour.setHeldItem(transported);
			player.setItemInHand(hand, ItemStack.EMPTY);
			AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
		}

		behaviour.tileEntity.notifyUpdate();
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasTileEntity() || state.getBlock() == newState.getBlock())
			return;
		ChargerBehaviour behaviour = get(worldIn, pos);
		if (behaviour == null)
			return;
		ItemHelper.dropContents(worldIn, pos, behaviour.processingOutputBuffer);
		for (TransportedItemStack transportedItemStack : behaviour.incoming)
			InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), transportedItemStack.stack);
		if (!behaviour.getHeldItemStack()
			.isEmpty())
			InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), behaviour.getHeldItemStack());
		worldIn.removeBlockEntity(pos);
	}

	@Override
	public void updateEntityAfterFallOn(IBlockReader worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;
		if (entityIn.level.isClientSide)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		DirectBeltInputBehaviour inputBehaviour =
			TileEntityBehaviour.get(worldIn, entityIn.blockPosition(), DirectBeltInputBehaviour.TYPE);
		if (inputBehaviour == null)
			return;
		ItemStack remainder = inputBehaviour.handleInsertion(itemEntity.getItem(), Direction.DOWN, false);
		itemEntity.setItem(remainder);
		if (remainder.isEmpty())
			itemEntity.remove();
	}
	
	protected static ChargerBehaviour get(IBlockReader worldIn, BlockPos pos) {
		return TileEntityBehaviour.get(worldIn, pos, ChargerBehaviour.TYPE);
	}*/
	
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		return IComparatorOverride.getComparetorOverride(worldIn, pos);
	}
}
