package com.hardlands.menu.screen.scenario;

import com.hardlands.HardlandsPlugin;
import com.hardlands.item.InventoryItem;
import com.hardlands.item.ItemBuilder;
import com.hardlands.menu.Menu;
import com.hardlands.menu.MenuAction;
import com.hardlands.menu.MenuInventory;
import com.hardlands.scenario.Scenario;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.UHC;
import com.hardlands.util.ChatMessenger;
import com.hardlands.util.option.Option;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScenarioOptionsMenu implements Menu {

    private static final MenuInventory.Size SIZE = MenuInventory.Size.SIX_ROWS;
    private static final int OPTIONS_PER_PAGE = 28;
    private static final int NEXT_SLOT = 53;

    private final ScenarioType type;
    private final int page;

    public ScenarioOptionsMenu(ScenarioType type) {
        this(type, 0);
    }

    private ScenarioOptionsMenu(ScenarioType type, int page) {
        this.type = type;
        this.page = page;
    }

    @Override
    public String displayName() {
        return this.type.getDisplayName();
    }

    @Override
    public Material outline() {
        return Material.PINK_STAINED_GLASS_PANE;
    }

    @Override
    public MenuInventory.Size size() {
        return SIZE;
    }

    @Override
    public Menu parent() {
        return HardlandsMenu.SCENARIOS;
    }

    @Override
    public void build(MenuInventory menu, Player player) {
        Scenario scenario = HardlandsPlugin.getInstance().getScenarioManager().get(this.type);
        List<Map.Entry<String, Option<?>>> options = List.copyOf(scenario.getContainer().getOptions().entrySet());

        int start = this.page * OPTIONS_PER_PAGE;
        int end = Math.min(start + OPTIONS_PER_PAGE, options.size());

        for (int index = start; index < end; index++) {
            this.renderOption(menu, options.get(index), index - start);
        }

        if (end < options.size()) {
            menu.item(NEXT_SLOT, InventoryItem.NEXT.getItem(), MenuAction.click("continuar", p -> new ScenarioOptionsMenu(this.type, this.page + 1).open(p, menu)));
        }
    }

    private void renderOption(MenuInventory menu, Map.Entry<String, Option<?>> entry, int index) {
        String key = entry.getKey();
        Option<?> option = entry.getValue();
        int slot = SIZE.slot(2 + index / 7, 1 + index % 7);

        menu.item(slot, createOptionItem(key, option), createOptionAction(menu, slot, key, option));
    }

    private static @Nullable MenuAction createOptionAction(MenuInventory menu, int slot, String key, Option<?> option) {
        return switch (option.getValue()) {
            case Boolean _ -> MenuAction.left("alternar", player -> handleOptionClick(player, menu, slot, key, option, ClickType.LEFT));
            case Integer _ -> MenuAction.leftRight("aumentar", "reducir", (player, click) -> handleOptionClick(player, menu, slot, key, option, click)).appendLore("<gray>Mantén Shift para modificar ±10.");
            case Float _, Double _ -> MenuAction.leftRight("aumentar", "reducir", (player, click) -> handleOptionClick(player, menu, slot, key, option, click)).appendLore("<gray>Mantén Shift para modificar ±1.0.");
            default -> null;
        };
    }

    private static void handleOptionClick(Player player, MenuInventory menu, int slot, String key, Option<?> option, ClickType click) {
        if (!canModify(player) || !changeOption(option, click)) return;
        menu.item(slot, createOptionItem(key, option), createOptionAction(menu, slot, key, option));
    }

    private static boolean canModify(Player player) {
        UHC uhc = HardlandsPlugin.getInstance().getUhc();

        if (uhc == null || !uhc.isRunning()) return true;

        ChatMessenger.sendMessage(player, "<red>No puedes modificar escenarios mientras la UHC está en curso.");
        return false;
    }

    private static boolean changeOption(Option<?> option, ClickType click) {
        Object previous = option.getValue();
        if (previous == null) return false;

        boolean changed = switch (previous) {
            case Boolean value -> changeBoolean(option, value, click);
            case Integer value -> changeInteger(option, value, click);
            case Float value -> changeFloat(option, value, click);
            case Double value -> changeDouble(option, value, click);
            default -> false;
        };

        if (!changed) return false;
        if (option.isValid()) return true;

        setValue(option, previous);
        return false;
    }

    private static boolean changeBoolean(Option<?> option, boolean value, ClickType click) {
        if (!click.isLeftClick()) return false;

        setValue(option, !value);
        return true;
    }

    private static boolean changeInteger(Option<?> option, int value, ClickType click) {
        if (!isNumericClick(click)) return false;

        int step = click.isShiftClick() ? 10 : 1;
        int delta = click.isLeftClick() ? step : -step;

        try {
            setValue(option, Math.addExact(value, delta));
            return true;
        } catch (ArithmeticException _) {
            return false;
        }
    }

    private static boolean changeFloat(Option<?> option, float value, ClickType click) {
        if (!isNumericClick(click)) return false;

        float step = click.isShiftClick() ? 1.0F : 0.1F;
        setValue(option, round(value + (click.isLeftClick() ? step : -step)));
        return true;
    }

    private static boolean changeDouble(Option<?> option, double value, ClickType click) {
        if (!isNumericClick(click)) return false;

        double step = click.isShiftClick() ? 1.0D : 0.1D;
        setValue(option, round(value + (click.isLeftClick() ? step : -step)));
        return true;
    }

    private static boolean isNumericClick(ClickType click) {
        return click.isLeftClick() || click.isRightClick();
    }

    private static ItemStack createOptionItem(String key, Option<?> option) {
        Object value = option.getValue();

        ItemBuilder builder = new ItemBuilder(material(value))
                .name("<#cc066c>" + displayKey(key))
                .lore("<gray>Valor: <white>" + formatValue(value), "<gray>Predeterminado: <white>" + formatValue(option.getDefaultValue()));

        if (Boolean.TRUE.equals(value)) builder.glint(true);

        return builder.build();
    }

    private static Material material(@Nullable Object value) {
        Material defaultMaterial = Material.PAPER;

        if (value == null) return defaultMaterial;

        return switch (value) {
            case Boolean enabled -> enabled ? Material.LIME_DYE : Material.GRAY_DYE;
            case Integer _ -> Material.CLOCK;
            case Float _, Double _ -> Material.COMPARATOR;
            case String _ -> Material.NAME_TAG;
            default -> defaultMaterial;
        };
    }

    private static String displayKey(String key) {
        String[] words = key.split("[_-]");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');

            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(Locale.ROOT));
        }

        return result.toString();
    }

    private static String formatValue(@Nullable Object value) {
        return switch (value) {
            case Float number -> String.format(Locale.ROOT, "%.2f", number);
            case Double number -> String.format(Locale.ROOT, "%.2f", number);
            case Boolean enabled -> enabled ? "Sí" : "No";
            case null -> "N/A";
            default -> value.toString();
        };
    }

    private static float round(float value) {
        return Math.round(value * 100.0F) / 100.0F;
    }

    private static double round(double value) {
        return Math.round(value * 100.0D) / 100.0D;
    }

    private static <T> void setValue(Option<?> option, T value) {
        ((Option<T>) option).setValue(value);
    }
}