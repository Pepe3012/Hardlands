package com.hardlands.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class ChatMessenger {
    private static final String PREFIX = "<#A4133C>Hardlands";

    private ChatMessenger() {}

    public static void sendMessage(Player player, String message) {
        String format = "<#1A1A1A>[%s<#1A1A1A>] <gray>» <white>%s";
        player.sendMessage(MiniMessage.miniMessage().deserialize(format.formatted(PREFIX, message)));
    }
}