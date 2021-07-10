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

public class Charger extends Block implements ITE<ChargerTileEntity>, IWrenchable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public static final VoxelShape CHARGER_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0, 0, 0, 16, 11, 16), Block.makeCuboidShape(1, 1, 1, 15, 13, 15));
	
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
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext c) {
		return getDefaultState().with(FACING, c.getPlayer().isSneaking() ? c.getPlacementHorizontalFacing().rotateYCCW() : c.getPlacementHorizontalFacing().rotateY());
	}

	@Override
	public ActionResultType onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(worldIn.isRemote())
			return ActionResultType.SUCCESS;
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null) {
			if(te instanceof ChargerTileEntity) {
				ChargerTileEntity cte = (ChargerTileEntity) te;
				ItemStack held = player.getHeldItem(handIn);
				if(cte.hasChargedStack() && held.isEmpty()) {
					InventoryHelper.spawnItemStack(worldIn, (double)player.getPositionVec().getX(), (double)player.getPositionVec().getY(), (double)player.getPositionVec().getZ(), cte.getChargedStack());
					//player.setHeldItem(handIn, cte.getChargedStack());
					cte.setChargedStack(ItemStack.EMPTY);
					worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
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
					worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					return ActionResultType.CONSUME;
				}
			}
		}
		return ActionResultType.PASS;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null) {
			if(te instanceof ChargerTileEntity) {
				ChargerTileEntity sste = (ChargerTileEntity) te;
				InventoryHelper.spawnItemStack(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), sste.getChargedStack());
			}
			te.remove();
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return IComparatorOverride.getComparetorOverride(worldIn, pos);
	}
}
