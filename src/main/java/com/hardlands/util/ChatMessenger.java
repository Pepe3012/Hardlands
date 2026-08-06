package com.hardlands.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public final class ChatMessenger {

    private static final String PREFIX = "<#A4133C>Hardlands";

    private ChatMessenger() {}

    public static void sendMessage(CommandSender sender, String message) {
        String format = "<dark_gray>[%s<dark_gray>] <gray>» <white>%s";
        sender.sendMessage(MiniMessage.miniMessage().deserialize(format.formatted(PREFIX, message)));
    }
}