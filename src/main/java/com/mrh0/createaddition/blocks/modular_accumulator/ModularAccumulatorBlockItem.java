package com.mrh0.createaddition.blocks.modular_accumulator;

import com.mrh0.createaddition.index.CATileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModularAccumulatorBlockItem extends BlockItem {

	public ModularAccumulatorBlockItem(Block block, Properties props) {
		super(block, props);
	}

	@Override
	public InteractionResult place(BlockPlaceContext ctx) {
		InteractionResult initialResult = super.place(ctx);
		if (!initialResult.consumesAction())
			return initialResult;
		tryMultiPlace(ctx);
		return initialResult;
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player,
		ItemStack stack, BlockState state) {
		MinecraftServer minecraftserver = level.getServer();
		if (minecraftserver == null)
			return false;
		CompoundTag nbt = stack.getTagElement("BlockEntityTag");
		if (nbt != null) {
			nbt.remove("Size");
			nbt.remove("Height");
			nbt.remove("Controller");
			nbt.remove("LastKnownPos");
			if (nbt.contains("EnergyContent")) {
				long energy = nbt.getCompound("EnergyContent").getLong("energy");
				if (energy > 0) {
					energy = Math.min(ModularAccumulatorTileEntity.getCapacityMultiplier(), energy);
					CompoundTag ec = new CompoundTag();
					ec.putLong("energy", energy);
					nbt.put("EnergyContent", ec);
				}
			}
		}
		return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
	}

	private void tryMultiPlace(BlockPlaceContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null)
			return;
		if (player.isShiftKeyDown())
			return;
		Direction face = ctx.getClickedFace();
		if (!face.getAxis()
			.isVertical())
			return;
		ItemStack stack = ctx.getItemInHand();
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockPos placedOnPos = pos.relative(face.getOpposite());
		BlockState placedOnState = world.getBlockState(placedOnPos);

		if (!ModularAccumulatorBlock.isAccumulator(placedOnState))
			return;
		ModularAccumulatorTileEntity accumulatorAt = CAConnectivityHandler.partAt(CATileEntities.MODULAR_ACCUMULATOR.get(), world, placedOnPos);
		if (accumulatorAt == null)
			return;
		ModularAccumulatorTileEntity controllerTE = accumulatorAt.getControllerBE();
		if (controllerTE == null)
			return;

		int width = controllerTE.width;
		if (width == 1)
			return;

		int blocksToPlace = 0;
		BlockPos startPos = face == Direction.DOWN ? controllerTE.getBlockPos()
			.below()
			: controllerTE.getBlockPos()
				.above(controllerTE.height);

		if (startPos.getY() != pos.getY())
			return;

		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState blockState = world.getBlockState(offsetPos);
				if (ModularAccumulatorBlock.isAccumulator(blockState))
					continue;
				if (!blockState.canBeReplaced())
					return;
				blocksToPlace++;
			}
		}

		if (!player.isCreative() && stack.getCount() < blocksToPlace)
			return;

		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState blockState = world.getBlockState(offsetPos);
				if (ModularAccumulatorBlock.isAccumulator(blockState))
					continue;
				BlockPlaceContext context = BlockPlaceContext.at(ctx, offsetPos, face);
				player.getCustomData()
					.putBoolean("SilenceTankSound", true);
				super.place(context);
				player.getCustomData()
					.remove("SilenceTankSound");
			}
		}
	}

	private static int getOrCreateEnergy(ItemStack stack) {
		var tag = stack.getTag();
		if(tag == null)
			tag = new CompoundTag();
		if(!tag.contains("energy", Tag.TAG_STRING))
			tag.putInt("energy", 0);
		return tag.getInt("energy");
	}
}
