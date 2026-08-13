package org.heather.hardlands.common.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public final class ItemBuilder {

    private static final int LORE_CHARACTER_LIMIT = 30;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final ComponentFlattener COMPONENT_FLATTENER = ComponentFlattener.basic();

    private final ItemStack item;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
    }

    public ItemBuilder name(String name) {
        return this.name(MINI_MESSAGE.deserialize(name));
    }

    public ItemBuilder name(Component name) {
        this.item.setData(DataComponentTypes.CUSTOM_NAME, nonItalic(name));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(deserializeLore(lines)));
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        ItemLore current = this.item.getData(DataComponentTypes.LORE);
        List<Component> lore = new ArrayList<>(current == null ? List.of() : current.lines());

        lore.addAll(deserializeLore(lines));
        this.item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

        return this;
    }

    public ItemBuilder glint(boolean glint) {
        this.item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        this.item.editMeta(SkullMeta.class, meta -> meta.setPlayerProfile(Bukkit.createProfile(owner)));
        return this.hideTooltip(DataComponentTypes.PROFILE);
    }

    public ItemBuilder hideTooltip(DataComponentType... components) {
        TooltipDisplay current = this.item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

        if (current != null) {
            builder.hideTooltip(current.hideTooltip());
            builder.hiddenComponents(current.hiddenComponents());
        }

        this.item.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.addHiddenComponents(components).build());
        return this;
    }

    public ItemBuilder setId(String key, String value) {
        return this.setId(key, PersistentDataType.STRING, value);
    }

    public ItemBuilder setId(String key, int value) {
        return this.setId(key, PersistentDataType.INTEGER, value);
    }

    public ItemBuilder setId(String key, double value) {
        return this.setId(key, PersistentDataType.DOUBLE, value);
    }

    public <T> ItemBuilder setId(String key, PersistentDataType<?, T> type, T value) {
        this.item.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey("hardlands", key), type, value);
        });

        return this;
    }

    public <T> ItemBuilder setId(NamespacedKey key, PersistentDataType<?, T> type, T value) {
        this.item.editMeta(meta -> meta.getPersistentDataContainer().set(key, type, value));
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    private static List<Component> deserializeLore(String[] lines) {
        return Stream.of(lines)
                .flatMap(line -> wrapLore(MINI_MESSAGE.deserialize(line)).stream())
                .map(ItemBuilder::nonItalic)
                .toList();
    }

    private static List<Component> wrapLore(Component component) {
        List<StyledCharacter> characters = flatten(component);

        if (characters.isEmpty()) {
            return List.of(Component.empty());
        }

        List<Component> lines = new ArrayList<>();
        List<StyledCharacter> currentLine = new ArrayList<>();
        List<StyledCharacter> currentWord = new ArrayList<>();

        boolean spaceBeforeWord = false;

        for (StyledCharacter character : characters) {
            int codePoint = character.codePoint();

            if (isLineBreak(codePoint)) {
                appendWord(lines, currentLine, currentWord, spaceBeforeWord);
                flushLine(lines, currentLine);
                spaceBeforeWord = false;
                continue;
            }

            if (Character.isWhitespace(codePoint)) {
                appendWord(lines, currentLine, currentWord, spaceBeforeWord);
                spaceBeforeWord = !currentLine.isEmpty();
                continue;
            }

            if (currentWord.isEmpty()) {
                currentWord = new ArrayList<>();
            }

            currentWord.add(character);
        }

        appendWord(lines, currentLine, currentWord, spaceBeforeWord);

        if (!currentLine.isEmpty()) {
            flushLine(lines, currentLine);
        }

        return lines.isEmpty() ? List.of(Component.empty()) : lines;
    }

    private static void appendWord(List<Component> lines, List<StyledCharacter> currentLine,
                                   List<StyledCharacter> word, boolean spaceBeforeWord) {
        if (word.isEmpty()) {
            return;
        }

        int requiredLength = word.size() + (spaceBeforeWord && !currentLine.isEmpty() ? 1 : 0);

        if (!currentLine.isEmpty() && currentLine.size() + requiredLength > LORE_CHARACTER_LIMIT) {
            flushLine(lines, currentLine);
        }

        if (!currentLine.isEmpty() && spaceBeforeWord) {
            StyledCharacter first = word.getFirst();
            currentLine.add(new StyledCharacter(' ', first.styles()));
        }

        currentLine.addAll(word);
        word.clear();
    }

    private static void flushLine(List<Component> lines, List<StyledCharacter> characters) {
        lines.add(buildComponent(characters));
        characters.clear();
    }

    private static Component buildComponent(List<StyledCharacter> characters) {
        if (characters.isEmpty()) {
            return Component.empty();
        }

        Component result = Component.empty();
        int start = 0;

        while (start < characters.size()) {
            StyledCharacter first = characters.get(start);
            int end = start + 1;

            while (end < characters.size() && first.styles().equals(characters.get(end).styles())) {
                end++;
            }

            StringBuilder text = new StringBuilder();

            for (int index = start; index < end; index++) {
                text.appendCodePoint(characters.get(index).codePoint());
            }

            result = result.append(applyStyles(text.toString(), first.styles()));
            start = end;
        }

        return result;
    }

    private static Component applyStyles(String text, List<Style> styles) {
        Component component = Component.text(text);

        for (int index = styles.size() - 1; index >= 0; index--) {
            component = Component.empty()
                    .style(styles.get(index))
                    .append(component);
        }

        return component;
    }

    private static List<StyledCharacter> flatten(Component component) {
        List<StyledCharacter> characters = new ArrayList<>();
        Deque<Style> styles = new ArrayDeque<>();

        COMPONENT_FLATTENER.flatten(component, new FlattenerListener() {

            @Override
            public void pushStyle(Style style) {
                styles.addLast(style);
            }

            @Override
            public void component(String text) {
                List<Style> activeStyles = List.copyOf(styles);
                text.codePoints().forEach(codePoint -> characters.add(new StyledCharacter(codePoint, activeStyles)));
            }

            @Override
            public void popStyle(Style style) {
                styles.removeLast();
            }
        });

        return characters;
    }

    private static boolean isLineBreak(int codePoint) {
        return codePoint == '\n' || codePoint == '\r';
    }

    private static Component nonItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    private record StyledCharacter(int codePoint, List<Style> styles) {}
}