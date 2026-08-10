package com.hardlands.common.menu.screen;

import com.hardlands.HardlandsPlugin;
import com.hardlands.common.item.InventoryItem;
import com.hardlands.common.menu.MenuAction;
import com.hardlands.common.menu.MenuInventory;
import com.hardlands.common.menu.screen.scenario.ScenariosMenu;
import com.hardlands.uhc.PreparationManager;
import com.hardlands.game.GameManager;
import com.hardlands.common.util.ChatMessenger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class MainMenu extends BaseMenu {

    public static final MainMenu INSTANCE = new MainMenu();

    private static final MenuInventory.Size SIZE = MenuInventory.Size.SIX_ROWS;

    private static final int SCENARIOS_SLOT = SIZE.slot(3, 1);
    private static final int PLAYERS_SLOT = SIZE.slot(3, 3);
    private static final int WORLD_BORDER_SLOT = SIZE.slot(3, 5);
    private static final int SETTINGS_SLOT = SIZE.slot(3, 7);

    private static final int TEMPLATES_SLOT = SIZE.slot(5, 2);
    private static final int VANILLA_CHANGES_SLOT = SIZE.slot(5, 6);

    public static final int PREPARATION_SLOT = SIZE.slot(6, 4);

    private MainMenu() {
        super("Hardlands", Material.RED_STAINED_GLASS_PANE, SIZE, null);
    }

    @Override
    public void build(MenuInventory menu, Player player) {
        menu.item(SCENARIOS_SLOT, InventoryItem.SCENARIOS.getItem(),
                MenuAction.click("administrar", p -> ScenariosMenu.INSTANCE.open(p, menu)));

        menu.item(PLAYERS_SLOT, InventoryItem.PLAYERS.getItem(),
                MenuAction.click("administrar", p -> PlayersMenu.INSTANCE.open(p, menu)));

        menu.item(WORLD_BORDER_SLOT, InventoryItem.WORLD_BORDER.getItem(),
                MenuAction.click("configurar", p -> WorldBorderMenu.INSTANCE.open(p, menu)));

        menu.item(SETTINGS_SLOT, InventoryItem.SETTINGS.getItem(),
                MenuAction.click("configurar", p -> SettingsMenu.INSTANCE.open(p, menu)));

        menu.item(TEMPLATES_SLOT, InventoryItem.TEMPLATES.getItem(),
                MenuAction.click("administrar", p -> TemplatesMenu.INSTANCE.open(p, menu)));

        menu.item(VANILLA_CHANGES_SLOT, InventoryItem.VANILLA_CHANGES.getItem(),
                MenuAction.click("configurar", p -> VanillaChangesMenu.INSTANCE.open(p, menu)));

        refreshPreparation(menu);
    }

    public static void refreshPreparation(MenuInventory menu) {
        PreparationManager preparation = getPreparationManager();
        menu.item(PREPARATION_SLOT, InventoryItem.PREPARATION.getItem(preparation), preparationAction(menu, preparation));
    }

    private static @Nullable PreparationManager getPreparationManager() {
        GameManager gameManager = HardlandsPlugin.getInstance().getUhc();
        return gameManager == null ? null : gameManager.getPreparationManager();
    }

    private static @Nullable MenuAction preparationAction(MenuInventory menu, @Nullable PreparationManager preparation) {
        if (preparation == null) return null;

        return switch (preparation.getState()) {
            case NOT_STARTED -> MenuAction.left("iniciar la preparación", player -> startPreparation(player, menu, preparation));
            case IN_PROGRESS -> MenuAction.right("cancelar la preparación", player -> cancelPreparation(player, menu, preparation));
            case COMPLETED -> null;
        };
    }

    private static void startPreparation(Player player, MenuInventory menu, PreparationManager preparation) {
        try {
            preparation.startPreparation();
            ChatMessenger.sendMessage(player, "<green>Se inició la pregeneración del mundo.");
            refreshPreparation(menu);
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, "<red>" + exception.getMessage());
        }
    }

    private static void cancelPreparation(Player player, MenuInventory menu, PreparationManager preparation) {
        try {
            preparation.cancelPreparation();
            ChatMessenger.sendMessage(player, "<yellow>Se canceló la pregeneración del mundo.");
            refreshPreparation(menu);
        } catch (IllegalStateException exception) {
            ChatMessenger.sendMessage(player, "<red>" + exception.getMessage());
        }
    }
}