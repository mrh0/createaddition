package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.crops.HarmfulPlantBlock;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class GameEvents {

	@SubscribeEvent
	public static void worldTickEvent(TickEvent.LevelTickEvent evt) {
		if(evt.level.isClientSide()) return;
		if(evt.phase == Phase.END) return;
		EnergyNetworkManager.tickWorld(evt.level);
	}

	@SubscribeEvent
	public static void serverTickEvent(TickEvent.ServerTickEvent evt) {
		if(evt.phase == Phase.END) return;
		// Using ServerTick instead of WorldTick because some contraptions can switch worlds.
		PortableEnergyManager.tick();
	}

	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent evt) {
		if(evt.phase == Phase.START) return;
		ObservePacket.tick();
		CADebugger.tick();
	}

	@SubscribeEvent
	public static void loadEvent(LevelEvent.Load evt) {
		if(evt.getLevel().isClientSide()) return;
		new EnergyNetworkManager(evt.getLevel());
	}

	@SubscribeEvent
    public static void interact(PlayerInteractEvent.RightClickBlock evt) {
		try {
			if(evt.getLevel().isClientSide()) return;
			BlockState state = evt.getLevel().getBlockState(evt.getPos());
			if(evt.getItemStack().getItem() == CAItems.STRAW.get() && evt.getLevel().getBlockEntity(evt.getPos()) instanceof BlazeBurnerBlockEntity) {
				if(state.is(AllBlocks.BLAZE_BURNER.get())) {
					BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
							.setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
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
