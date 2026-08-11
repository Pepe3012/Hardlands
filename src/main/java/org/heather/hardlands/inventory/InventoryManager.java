package org.heather.hardlands.inventory;

import org.bukkit.entity.Player;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.inventory.screen.InventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class InventoryManager {

    private final Map<InventoryScreenType, Inventory> inventories = new EnumMap<>(InventoryScreenType.class);

    private final Hardlands plugin;

    public InventoryManager(final Hardlands plugin) {
        this.plugin = plugin;
    }

    public void registerInventories(InventoryScreenType... definitions) {
        for (InventoryScreenType definition : definitions) {
            if (this.inventories.containsKey(definition)) {
                throw new IllegalArgumentException("Menu is already registered: " + definition);
            }

            this.inventories.put(definition, definition.createInventory(this.plugin));
        }
    }

    public void openInventory(InventoryScreenType definition, Player player) {
        this.openInventory(definition, player, null);
    }

    public void openInventory(InventoryScreenType definition, Player player, @Nullable InventoryScreen previous) {
        this.getInventoryDefinition(definition).open(player, previous);
    }

    public Inventory getInventoryDefinition(InventoryScreenType definition) {
        Inventory menu = this.inventories.get(definition);

        if (menu == null) {
            throw new IllegalArgumentException("Menu is not registered: " + definition);
        }

        return menu;
    }

    public List<InventoryScreenType> getInventoryDefinitions() {
        return List.copyOf(this.inventories.keySet());
    }
}