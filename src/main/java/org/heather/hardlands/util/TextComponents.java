package org.heather.hardlands.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public final class TextComponents {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    private TextComponents() {}

    public static Component parse(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    public static String toPlainText(Component component) {
        return PLAIN_TEXT.serialize(component);
    }

    public static String formatPlayer(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}