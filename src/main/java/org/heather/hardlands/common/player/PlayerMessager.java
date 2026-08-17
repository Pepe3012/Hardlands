package org.heather.hardlands.common.player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerMessager {

    private static final String FORMAT = "<dark_gray>[%s<dark_gray>] <gray>» <white>";
    private static final String PREFIX = FORMAT.formatted("<#A4133C>Hardlands");

    private PlayerMessager() {}

    public static void broadcastMessage(String message) {
        for (var player : Bukkit.getOnlinePlayers()) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(PREFIX + message));
    }
}