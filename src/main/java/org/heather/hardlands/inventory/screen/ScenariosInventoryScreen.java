package org.heather.hardlands.inventory.screen;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.common.item.ItemBuilder;
import org.heather.hardlands.scenario.ScenarioDefinition;
import org.heather.hardlands.scenario.ScenarioManager;

public final class ScenariosInventoryScreen extends InventoryScreen {

    private static final int FIRST_SLOT = 10;
    private static final int LAST_SLOT = 43;
    private static final int COLUMNS = 9;

    public ScenariosInventoryScreen(final Hardlands plugin, Definition definition) {
        super(plugin, definition);
    }

    @Override
    protected void onInitialize(Inventory inventory, Player player) {
        ScenarioManager manager = super.plugin.getScenarioManager();
        int slot = FIRST_SLOT;

        for (ScenarioDefinition definition : manager.getRegisteredScenarioDefinitions()) {
            slot = this.nextContentSlot(slot);

            if (slot > LAST_SLOT) {
                break;
            }

            inventory.setItem(slot++, this.createScenarioItem(manager, definition));
        }
    }

    private ItemStack createScenarioItem(ScenarioManager controller, ScenarioDefinition definition) {
        boolean active = controller.isScenarioActive(definition);

        return new ItemBuilder(definition.getDisplay().material())
                .name((active ? "<green>" : "<red>") + definition.getDisplayName())
                .lore(
                        "<gray>" + definition.getDisplay().lore(),
                        "",
                        "<gray>Estado: " + (active ? "<green>Activo" : "<red>Inactivo")
                )
                .glint(active)
                .build();
    }

    private int nextContentSlot(int slot) {
        while (slot <= LAST_SLOT && (slot % COLUMNS == 0 || slot % COLUMNS == COLUMNS - 1)) {
            slot++;
        }

        return slot;
    }
}