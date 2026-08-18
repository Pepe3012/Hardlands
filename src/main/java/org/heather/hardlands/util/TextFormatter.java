package org.heather.hardlands.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public final class TextFormatter {

    private TextFormatter() {}

    public static Component parse(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static String toPlainText(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static String formatPlayer(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}