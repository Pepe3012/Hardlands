package com.hardlands.menu.screen.scenario;

import com.hardlands.HardlandsPlugin;
import com.hardlands.item.InventoryItem;
import com.hardlands.item.ItemBuilder;
import com.hardlands.menu.MenuAction;
import com.hardlands.menu.MenuInventory;
import com.hardlands.scenario.Scenario;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.UHC;
import com.hardlands.util.ChatMessenger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class ScenarioMenuContent {

    private static final int SCENARIOS_PER_PAGE = 28;
    private static final int PREVIOUS_SLOT = 45;
    private static final int NEXT_SLOT = 53;

    private ScenarioMenuContent() {}

    static void build(MenuInventory menu, int page) {
        ScenarioManager manager = manager();
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

    private static ItemStack createItem(ScenarioType type, ScenarioManager manager) {
        boolean active = manager.isActive(type);

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

    private static MenuAction createAction(MenuInventory menu, ScenarioType type, int page, ScenarioManager manager) {
        boolean active = manager.isActive(type);
        String action = active ? "desactivar" : "activar";

        Scenario scenario = manager.get(type);

        if (scenario.getContainer().getOptions().isEmpty()) {
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

        boolean enabled = manager().toggle(type);

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
        UHC uhc = HardlandsPlugin.getInstance().getUhc();
        if (uhc == null || !uhc.isRunning()) return true;

        ChatMessenger.sendMessage(player, "<red>No puedes modificar escenarios mientras la UHC está en curso.");
        return false;
    }

    private static ScenarioManager manager() {
        return HardlandsPlugin.getInstance().getScenarioManager();
    }
}