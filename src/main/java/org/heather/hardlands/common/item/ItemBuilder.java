package org.heather.hardlands.common.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class ItemBuilder {

    private static final int LORE_CHARACTER_LIMIT = 25;

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
    }

    public ItemBuilder name(String name) {
        return name(MiniMessage.miniMessage().deserialize(name));
    }

    public ItemBuilder name(Component name) {
        item.setData(DataComponentTypes.CUSTOM_NAME, nonItalic(name));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        item.setData(DataComponentTypes.LORE, ItemLore.lore(deserializeLore(lines)));
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        ItemLore current = item.getData(DataComponentTypes.LORE);
        List<Component> lore = new ArrayList<>((current == null ? 0 : current.lines().size()) + lines.length);

        if (current != null) {
            lore.addAll(current.lines());
        }
        lore.addAll(deserializeLore(lines));

        item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        return this;
    }

    public ItemBuilder glint(boolean glint) {
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        item.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(Bukkit.createProfile(owner)));
        return this;
    }

    public ItemBuilder setId(String key, String value) {
        return setId(key, PersistentDataType.STRING, value);
    }

    public ItemBuilder setId(String key, int value) {
        return setId(key, PersistentDataType.INTEGER, value);
    }

    public ItemBuilder setId(String key, double value) {
        return setId(key, PersistentDataType.DOUBLE, value);
    }

    public <T> ItemBuilder setId(String key, PersistentDataType<?, T> type, T value) {
        item.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey("hardlands", key), type, value);
        });
        return this;
    }

    public <T> ItemBuilder setId(NamespacedKey key, PersistentDataType<?, T> type, T value) {
        item.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, type, value);
        });
        return this;
    }

    public ItemStack build() {
        return item;
    }

    private static List<Component> deserializeLore(String[] lines) {
        return Stream.of(lines)
                .flatMap(line -> wrapLore(line).stream())
                .map(line -> nonItalic(MiniMessage.miniMessage().deserialize(line)))
                .toList();
    }

    private static List<String> wrapLore(String text) {
        if (text.isBlank()) {
            return List.of(text);
        }

        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int lineLength = 0;
        int index = 0;

        while (index < text.length()) {
            LoreWord word = readWord(text, index);
            index = word.nextIndex();

            if (word.content().isEmpty()) {
                continue;
            }

            if (word.lineBreakBefore() || shouldWrap(lineLength, word.visibleLength())) {
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                    current.setLength(0);
                }

                lineLength = 0;
            }

            if (lineLength > 0 && word.visibleLength() > 0) {
                current.append(' ');
                lineLength++;
            }

            current.append(word.content());
            lineLength += word.visibleLength();
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private static LoreWord readWord(String text, int startIndex) {
        int index = startIndex;
        boolean lineBreakBefore = false;

        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            lineBreakBefore |= isLineBreak(text.charAt(index));
            index++;
        }

        int wordStart = index;
        int visibleLength = 0;
        boolean insideTag = false;

        while (index < text.length() && (insideTag || !Character.isWhitespace(text.charAt(index)))) {
            char c = text.charAt(index);

            if (!insideTag && c == '<' && !isEscaped(text, index)) {
                insideTag = true;
            } else if (insideTag && c == '>' && !isEscaped(text, index)) {
                insideTag = false;
            } else if (!insideTag) {
                visibleLength++;
            }

            index++;
        }

        return new LoreWord(text.substring(wordStart, index), visibleLength, index, lineBreakBefore);
    }

    private static int appendWord(StringBuilder result, LoreWord word, int lineLength) {
        if (word.visibleLength() == 0) {
            result.append(word.content());
            return lineLength;
        }

        if (shouldWrap(lineLength, word.visibleLength())) {
            result.append('\n');
            lineLength = 0;
        } else if (lineLength > 0) {
            result.append(' ');
            lineLength++;
        }

        result.append(word.content());
        return lineLength + word.visibleLength();
    }

    private static boolean shouldWrap(int lineLength, int wordLength) {
        return lineLength > 0 && lineLength + 1 + wordLength > LORE_CHARACTER_LIMIT;
    }

    private static boolean isEscaped(String text, int index) {
        int backslashes = 0;
        for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            backslashes++;
        }
        return backslashes % 2 != 0;
    }

    private static boolean isLineBreak(char character) {
        return character == '\n' || character == '\r';
    }

    private static Component nonItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    private record LoreWord(String content, int visibleLength, int nextIndex, boolean lineBreakBefore) {}
}