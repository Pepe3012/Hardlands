package org.heather.hardlands.text;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ChatMessenger {

    private static final String PREFIX = "<dark_gray>[%sHardlands<dark_gray>] <gray>» <white>".formatted(HardlandsColor.BRAND.primary());

    private ChatMessenger() {}

    public static void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, message);
        }
    }

    public static void send(Player player, String message) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(PREFIX + message));
    }
}