package com.mrh0.createaddition.blocks.charger;

import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.index.CATileEntities;
import com.mrh0.createaddition.util.IComparatorOverride;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
				if(cte.hasChargedStack() && held.isEmpty()) {
					InventoryHelper.dropItemStack(worldIn, (double)player.position().x(), (double)player.position().y(), (double)player.position().z(), cte.getChargedStack());
					//player.setHeldItem(handIn, cte.getChargedStack());
					cte.setChargedStack(ItemStack.EMPTY);
					worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					return ActionResultType.CONSUME;
				}
				else {
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
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		return IComparatorOverride.getComparetorOverride(worldIn, pos);
	}
}
