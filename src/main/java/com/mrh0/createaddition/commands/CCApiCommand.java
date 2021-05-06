package com.mrh0.createaddition.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class CCApiCommand {
	public static void register(CommandDispatcher<CommandSource> dispather) {
		dispather.register(Commands.literal("cca_api").requires(source -> source.hasPermissionLevel(0))
			.executes(context -> {
				PlayerEntity p =  context.getSource().asPlayer();
				String link = "https://github.com/mrh0/createaddition/blob/main/COMPUTERCRAFT.md";
				TextComponent text = new TranslationTextComponent("createaddition.command.cca_api.link");
				text.styled(style -> {
					return style.withFormatting(TextFormatting.AQUA, TextFormatting.UNDERLINE)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(link)))
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
				});
				p.sendMessage(text, PlayerEntity.getUUID(p.getGameProfile()));
				return 1;
			}
		));
	}
}
