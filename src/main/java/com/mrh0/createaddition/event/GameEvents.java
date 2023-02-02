package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementManager;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GameEvents {
	@SubscribeEvent
	public static void worldTickEvent(TickEvent.WorldTickEvent evt) {
		if(evt.world.isClientSide()) return;
		if(evt.phase == Phase.END) return;
		EnergyNetworkManager.tickWorld(evt.world);
		ConnectorMovementManager.tickWorld(evt.world);
	}
	
	@SubscribeEvent
	public static void clientTickEvent(TickEvent.ClientTickEvent evt) {
		if(evt.phase == Phase.START) return;
		ObservePacket.tick();
	}
	
	@SubscribeEvent
	public static void loadEvent(WorldEvent.Load evt) {
		if(evt.getWorld().isClientSide())
			return;
		new EnergyNetworkManager(evt.getWorld());
		new ConnectorMovementManager(evt.getWorld());
	}
	
	@SubscribeEvent
    public static void interact(PlayerInteractEvent.RightClickBlock evt) {
		try {
			if(evt.getWorld().isClientSide()) return;
			BlockState state = evt.getWorld().getBlockState(evt.getPos());
			if(evt.getItemStack().getItem() == CAItems.STRAW.get()) {
				if(state.is(AllBlocks.BLAZE_BURNER.get())) {
					BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
							.setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
							.setValue(LiquidBlazeBurnerBlock.FACING, state.getValue(BlazeBurnerBlock.FACING));
					evt.getWorld().setBlockAndUpdate(evt.getPos(), newState);
					//if(!evt.getEntity().isCreative())
						evt.getItemStack().shrink(1);
					evt.setCancellationResult(InteractionResult.SUCCESS);
	            	evt.setCanceled(true);
				}
			}
		}
		catch(Exception e)  {
			e.printStackTrace();
		}
	}
}
