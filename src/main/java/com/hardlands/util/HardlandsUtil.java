package com.hardlands.util;

import org.bukkit.entity.Player;

public final class HardlandsUtil {
    private HardlandsUtil() {}

    public static String getPlayerHeadAndName(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}
