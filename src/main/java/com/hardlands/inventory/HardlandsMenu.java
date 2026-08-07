package com.hardlands.inventory;

import com.hardlands.HardlandsPlugin;
import com.hardlands.item.InventoryItem;
import com.hardlands.uhc.PreparationManager;
import com.hardlands.util.ChatMessenger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public enum HardlandsMenu implements Menu {

    MAIN("Hardlands", null, Material.RED_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS) {

        @Override
        public void build(MenuInventory menu, Player player) {
            menu.item(SCENARIOS_SLOT, InventoryItem.SCENARIOS.getItem(), p -> SCENARIOS.open(p, menu));
            menu.item(TEAMS_SLOT, InventoryItem.TEAMS.getItem(), p -> TEAMS.open(p, menu));
            menu.item(WORLD_BORDER_SLOT, InventoryItem.WORLD_BORDER.getItem(), p -> WORLD_BORDER.open(p, menu));
            menu.item(SETTINGS_SLOT, InventoryItem.SETTINGS.getItem(), p -> SETTINGS.open(p, menu));
            menu.item(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(getPreparationManager()), (p, click) -> handlePreparationClick(p, menu, click));
        }
    },

    SCENARIOS("Escenarios", MAIN, Material.PINK_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS),
    TEAMS("Equipos", MAIN, Material.YELLOW_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS),
    WORLD_BORDER("Borde del mundo", MAIN, Material.LIGHT_BLUE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS),
    SETTINGS("Configuración", MAIN, Material.ORANGE_STAINED_GLASS_PANE, MenuInventory.Size.SIX_ROWS);

    private static final MenuInventory.Size MAIN_SIZE = MenuInventory.Size.SIX_ROWS;
    private static final int SCENARIOS_SLOT = MAIN_SIZE.slot(4, 1);
    private static final int TEAMS_SLOT = MAIN_SIZE.slot(4, 3);
    private static final int WORLD_BORDER_SLOT = MAIN_SIZE.slot(4, 5);
    private static final int SETTINGS_SLOT = MAIN_SIZE.slot(4, 7);
    public static final int PREPARATION_SLOT = MAIN_SIZE.slot(6, 4);

    private final String name;
    @Nullable private final HardlandsMenu parent; // narrower than Menu; every parent here is itself a HardlandsMenu
    private final Material outline;
    private final MenuInventory.Size size;

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public @Nullable Menu parent() {
        return parent;
    }

    @Override
    public Material outline() {
        return outline;
    }

    @Override
    public MenuInventory.Size size() {
        return size;
    }

    public static @Nullable PreparationManager getPreparationManager() {
        var uhc = HardlandsPlugin.getInstance().getUhc();
        return uhc == null ? null : uhc.getPreparationManager();
    }

    private static void handlePreparationClick(Player player, MenuInventory menu, ClickType click) {
        PreparationManager preparation = getPreparationManager();

        if (preparation == null) {
            ChatMessenger.sendMessage(player, "<red>No existe una sesión de UHC.");
            return;
        }

        try {
            if (click.isLeftClick()) {
                if (!startPreparation(player, preparation)) return;
            } else if (click.isRightClick()) {
                if (!cancelPreparation(player, preparation)) return;
            } else {
                return;
            }

            menu.setItem(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(preparation));
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, "<red>" + exception.getMessage());
        }
    }

    private static boolean startPreparation(Player player, PreparationManager preparation) {
        if (preparation.isInProgress()) {
            ChatMessenger.sendMessage(player, "<yellow>La pregeneración ya está en curso.");
            return false;
        }

        if (preparation.isCompleted()) {
            ChatMessenger.sendMessage(player, "<green>El mundo ya está preparado.");
            return false;
        }

        preparation.startPreparation();
        ChatMessenger.sendMessage(player, "<green>Se inició la pregeneración del mundo.");
        return true;
    }

    private static boolean cancelPreparation(Player player, PreparationManager preparation) {
        if (!preparation.isInProgress()) {
            ChatMessenger.sendMessage(player, "<red>No hay una pregeneración en curso.");
            return false;
        }

        preparation.cancelPreparation();
        ChatMessenger.sendMessage(player, "<yellow>Se canceló la pregeneración del mundo.");
        return true;
    }
}