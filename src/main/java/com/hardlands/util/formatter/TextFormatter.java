package com.hardlands.util.formatter;

import org.bukkit.entity.Player;

public final class TextFormatter {

    private TextFormatter() {}

    public static String getPlayerHeadAndName(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}
