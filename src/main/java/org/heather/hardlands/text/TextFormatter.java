package org.heather.hardlands.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.heather.hardlands.util.TinyCaps;

public final class TextFormatter {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT =
            PlainTextComponentSerializer.plainText();
    private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile("<([^<>\\s]+)>");

    private TextFormatter() {}

    public static Component parse(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    public static String toPlainText(Component component) {
        return PLAIN_TEXT.serialize(component);
    }

    public static Component formatHighlighted(String text) {
        TextComponent.Builder result = Component.text().color(NamedTextColor.WHITE);
        Matcher matcher = HIGHLIGHT_PATTERN.matcher(text);
        int position = 0;

        while (matcher.find()) {
            result.append(Component.text(text.substring(position, matcher.start())));
            result.append(Component.text(matcher.group(1), HardlandsColor.PRIMARY));
            position = matcher.end();
        }

        result.append(Component.text(text.substring(position)));
        return result.build();
    }

    public static Component formatTinyCaps(String text) {
        return Component.text(TinyCaps.format(text), HardlandsColor.PRIMARY);
    }

    public static String formatPlayer(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}
