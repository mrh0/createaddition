package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
		ServerTickEvents.START_SERVER_TICK.register(GameEvents::serverTickEvent);
		ServerWorldEvents.LOAD.register(GameEvents::loadEvent);
		UseBlockCallback.EVENT.register(GameEvents::interact);
	}

	public static void initClient() {
		ClientTickEvents.END_CLIENT_TICK.register(GameEvents::clientTickEvent);
	}

	public static void worldTickEvent(ServerLevel world) {
		EnergyNetworkManager.tickWorld(world);
	}

	public static void serverTickEvent(MinecraftServer server) {
		// Using ServerTick instead of WorldTick because some contraptions can switch worlds.
		PortableEnergyManager.tick();
	}

	public static void clientTickEvent(Minecraft ignoredClient) {
		ObservePacket.tick();
		CADebugger.tick();
	}

	public static void loadEvent(MinecraftServer server, ServerLevel world) {
		new EnergyNetworkManager(world);
	}

    public static InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		try {
			BlockPos pos = hitResult.getBlockPos();
			ItemStack item = player.getItemInHand(hand);
			if(level.isClientSide()) return InteractionResult.PASS;
			BlockState state = level.getBlockState(pos);
			if(item.getItem() == CAItems.STRAW.get()) {
				if(state.is(AllBlocks.BLAZE_BURNER.get()) && world.getBlockEntity(hitResult.getBlockPos()) instanceof BlazeBurnerBlockEntity) {
					BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
							.setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
							.setValue(LiquidBlazeBurnerBlock.FACING, state.getValue(BlazeBurnerBlock.FACING));
					level.setBlockAndUpdate(pos, newState);
					if(!player.isCreative())
						item.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return InteractionResult.PASS;
	}
}
