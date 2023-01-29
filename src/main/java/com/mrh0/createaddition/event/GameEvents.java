package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.connector.ConnectorMovementManager;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurner;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GameEvents {

	public static void initCommon() {
		ServerTickEvents.START_WORLD_TICK.register(GameEvents::worldTickEvent);
		ServerWorldEvents.LOAD.register(GameEvents::loadEvent);
		UseBlockCallback.EVENT.register(GameEvents::onBlockUseEvent);
	}


	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientTickEvents.END_CLIENT_TICK.register(GameEvents::clientTickEvent);
	}

	private static InteractionResult onBlockUseEvent(
			Player player,
			Level level,
			InteractionHand interactionHand,
			BlockHitResult blockHitResult
	) {
		BlockState state = level.getBlockState(blockHitResult.getBlockPos());
		ItemStack playerItem = player.getItemInHand(player.getUsedItemHand());
		if(playerItem.getItem() == CAItems.STRAW.get()) {
			if(state.is(AllBlocks.BLAZE_BURNER.get())) {
				BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
						.setValue(LiquidBlazeBurner.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
						.setValue(LiquidBlazeBurner.FACING, state.getValue(BlazeBurnerBlock.FACING));
				level.setBlockAndUpdate(blockHitResult.getBlockPos(), newState);
				if(!player.isCreative())
					playerItem.shrink(1);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	public static void worldTickEvent(ServerLevel world) {
		EnergyNetworkManager.tickWorld(world);
		ConnectorMovementManager.tickWorld(world);
	}

	@Environment(EnvType.CLIENT)
	public static void clientTickEvent(Minecraft ignoredClient) {
		ObservePacket.tick();
	}

	public static void loadEvent(MinecraftServer server, ServerLevel level) {
		new EnergyNetworkManager(level);
		new ConnectorMovementManager(level);
	}
}
