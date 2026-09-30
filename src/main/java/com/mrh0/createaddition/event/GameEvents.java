package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.mrh0.createaddition.energy.network.WireGraph;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class GameEvents {
	@SubscribeEvent
	public static void levelTickEvent(LevelTickEvent.Pre evt) {
		if(evt.getLevel().isClientSide()) return;
		// if (evt == Phase.END) return;
		WireGraph.tick(evt.getLevel());
	}

	@SubscribeEvent
	public static void serverTickEvent(ServerTickEvent.Pre evt) {
		//if (evt.phase == Phase.END) return;
		// Using ServerTick instead of WorldTick because some contraptions can switch worlds.
		PortableEnergyManager.tick();
	}

	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent.Post evt) {
		//if (evt.phase == Phase.START) return;
		ObservePacketPayload.tick();
		CADebugger.tick();
	}

	@SubscribeEvent
	public static void chunkLoadEvent(ChunkEvent.Load evt) {
		if (evt.getLevel() instanceof ServerLevel level && evt.getChunk() instanceof LevelChunk chunk)
			WireGraph.chunkLoaded(level, chunk);
	}

	@SubscribeEvent
	public static void LevelUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerLevel level) WireGraph.levelUnloaded(level);
	}

	@SubscribeEvent
    public static void interact(PlayerInteractEvent.RightClickBlock evt) {
		try {
			if(evt.getLevel().isClientSide()) return;
			BlockState state = evt.getLevel().getBlockState(evt.getPos());
			if(evt.getItemStack().getItem() == CAItems.STRAW.get() && evt.getLevel().getBlockEntity(evt.getPos()) instanceof BlazeBurnerBlockEntity) {
				if(state.is(AllBlocks.BLAZE_BURNER.get())) {
					BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
							.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
							.setValue(LiquidBlazeBurnerBlock.FACING, state.getValue(BlazeBurnerBlock.FACING));
					evt.getLevel().setBlockAndUpdate(evt.getPos(), newState);
					if(!evt.getEntity().isCreative())
						evt.getItemStack().shrink(1);
					evt.setCancellationResult(InteractionResult.SUCCESS);
	            	evt.setCanceled(true);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static final Direction[] horizontalDirections = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	/*@SubscribeEvent
	public static void grow(BlockEvent.CropGrowEvent.Pre evt) {
		try {
			double chance = 0.001d;
			for(Direction dir : horizontalDirections) {
				BlockState state = evt.getLevel().getBlockState(evt.getPos().relative(dir));
				if(state.is(CABlocks.HARMFUL_PLANT.get()))
					chance *= state.getValue(HarmfulPlantBlock.AGE)*3;
			}
			if(Math.random() < chance)
				evt.getLevel().setBlock(evt.getPos(), CABlocks.HARMFUL_PLANT.getDefaultState(), 3);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/
}
