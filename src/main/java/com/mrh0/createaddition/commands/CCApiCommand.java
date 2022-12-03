package com.mrh0.createaddition.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class CCApiCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispather) {
		dispather.register(Commands.literal("cca_api").requires(source -> source.hasPermission(0))
			.executes(context -> {
				Player p =  context.getSource().getPlayerOrException();
				String link = "https://github.com/mrh0/createaddition/blob/main/COMPUTERCRAFT.md";
				MutableComponent text = Component.translatable("createaddition.command.cca_api.link");
				text.withStyle(style -> {
					return style.applyFormats(ChatFormatting.AQUA, ChatFormatting.UNDERLINE)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(link)))
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
				});
				p.sendSystemMessage(text); //Player.createPlayerUUID(p.getGameProfile())
				return 1;
			}
		));
	}
}
