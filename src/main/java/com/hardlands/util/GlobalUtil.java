package com.hardlands.util;

import org.bukkit.entity.Player;

public final class GlobalUtil {
    private GlobalUtil() {}

    public static String getPlayerHeadAndName(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }

    public static long minutesToTicks(int minutes) {
        return (minutes * 60L) * 20L;
    }
}