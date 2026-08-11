package org.heather.hardlands.common.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ItemBuilder {

    private static final int LORE_CHARACTER_LIMIT = 15;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
    }

    public ItemBuilder(ItemStack item) {
        this.item = Objects.requireNonNull(item, "Item cannot be null").clone();
    }

    public ItemBuilder name(String name) {
        this.item.setData(
                DataComponentTypes.CUSTOM_NAME,
                nonItalic(MINI_MESSAGE.deserialize(name))
        );
        return this;
    }

    public ItemBuilder lore(String... lines) {
        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(deserializeLore(lines)));
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        ItemLore current = this.item.getData(DataComponentTypes.LORE);
        List<Component> lore = new ArrayList<>((current == null ? 0 : current.lines().size()) + lines.length);

        if (current != null) lore.addAll(current.lines());
        lore.addAll(deserializeLore(lines));

        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        return this;
    }

    public ItemBuilder glint(boolean glint) {
        this.item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        this.item.editMeta(
                SkullMeta.class,
                meta -> meta.setPlayerProfile(Bukkit.createProfile(owner))
        );
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    private static List<Component> deserializeLore(String[] lines) {
        List<Component> lore = new ArrayList<>(lines.length);

        for (String line : lines) {
            lore.add(nonItalic(MINI_MESSAGE.deserialize(wrapLore(line))));
        }

        return lore;
    }

    private static String wrapLore(String text) {
        if (text.isBlank()) return text;

        StringBuilder result = new StringBuilder(text.length());
        int lineLength = 0;
        int index = 0;

        while (index < text.length()) {
            LoreWord word = readWord(text, index);
            index = word.nextIndex();

            if (!word.content().isEmpty()) {
                if (word.lineBreakBefore()) {
                    result.append('\n');
                    lineLength = 0;
                }

                lineLength = appendWord(result, word, lineLength);
            }
        }

        return result.toString();
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
            char character = text.charAt(index);

            if (!insideTag && character == '<' && !isEscaped(text, index)) {
                insideTag = true;
            } else if (insideTag && character == '>' && !isEscaped(text, index)) {
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