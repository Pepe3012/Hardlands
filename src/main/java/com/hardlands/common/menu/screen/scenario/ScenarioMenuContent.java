package com.hardlands.common.menu.screen.scenario;

import com.hardlands.HardlandsPlugin;
import com.hardlands.common.item.InventoryItem;
import com.hardlands.common.item.ItemBuilder;
import com.hardlands.common.menu.MenuAction;
import com.hardlands.common.menu.MenuInventory;
import com.hardlands.scenario.ScenarioModule;
import com.hardlands.scenario.ScenarioController;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.game.GameManager;
import com.hardlands.common.util.ChatMessenger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class ScenarioMenuContent {

    private static final int SCENARIOS_PER_PAGE = 28;
    private static final int PREVIOUS_SLOT = 45;
    private static final int NEXT_SLOT = 53;

    private ScenarioMenuContent() {}

    static void build(MenuInventory menu, int page) {
        ScenarioController manager = manager();
        var types = manager.getRegisteredScenarioTypes();

        int start = page * SCENARIOS_PER_PAGE;
        int end = Math.min(start + SCENARIOS_PER_PAGE, types.size());

        for (int index = start; index < end; index++) {
            ScenarioType type = types.get(index);
            int pageIndex = index - start;
            int slot = menu.getSize().slot(2 + pageIndex / 7, 1 + pageIndex % 7);

            menu.item(slot, createItem(type, manager), createAction(menu, type, page, manager));
        }

        if (page > 0) {
            menu.item(PREVIOUS_SLOT, InventoryItem.PREVIOUS.getItem(),
                    MenuAction.click("regresar", player -> openPage(player, menu, page - 1)));
        }

        if (end < types.size()) {
            menu.item(NEXT_SLOT, InventoryItem.NEXT.getItem(),
                    MenuAction.click("continuar", player -> openPage(player, menu, page + 1)));
        }
    }

    private static ItemStack createItem(ScenarioType type, ScenarioController manager) {
        boolean active = manager.isScenarioActive(type);

        return new ItemBuilder(type.getMaterial())
                .name((active ? "<green>" : "<red>") + type.getDisplayName())
                .lore(
                        "<gray>" + type.getDescription(),
                        "",
                        "<gray>Estado: " + (active ? "<green>Activo" : "<red>Inactivo")
                )
                .glint(active)
                .build();
    }

    private static MenuAction createAction(MenuInventory menu, ScenarioType type, int page, ScenarioController manager) {
        boolean active = manager.isScenarioActive(type);
        String action = active ? "desactivar" : "activar";

        ScenarioModule scenarioModule = manager.get(type);

        if (scenarioModule.getOptionHolder().getOptions().isEmpty()) {
            return MenuAction.left(action, player -> toggle(player, menu, type, page));
        }

        return MenuAction.leftRight(
                action,
                player -> toggle(player, menu, type, page),
                "configurar",
                player -> new ScenarioOptionsMenu(type).open(player, menu)
        );
    }

    private static void toggle(Player player, MenuInventory menu, ScenarioType type, int page) {
        if (!canModify(player)) return;

        boolean enabled = manager().toggleScenario(type);

        ChatMessenger.sendMessage(player, enabled
                ? "<green>Escenario '" + type.getDisplayName() + "' activado."
                : "<yellow>Escenario '" + type.getDisplayName() + "' desactivado.");

        openPage(player, menu.getPrevious(), page);
    }

    private static void openPage(Player player, MenuInventory previous, int page) {
        if (page == 0) ScenariosMenu.INSTANCE.open(player, previous);
        else new ScenarioPageMenu(page).open(player, previous);
    }

    private static boolean canModify(Player player) {
        GameManager gameManager = HardlandsPlugin.getInstance().getUhc();
        if (gameManager == null || !gameManager.isRunning()) return true;

        ChatMessenger.sendMessage(player, "<red>No puedes modificar escenarios mientras la UHC está en curso.");
        return false;
    }

    private static ScenarioController manager() {
        return HardlandsPlugin.getInstance().getScenarioManager();
    }
}