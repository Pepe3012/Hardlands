package org.heather.hardlands.common.inventory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.Inventory;

public final class InventoryRegistry {

    private static final Map<InventoryDefinition, Inventory> INVENTORIES = new EnumMap<>(InventoryDefinition.class);

    private InventoryRegistry() {}

    public static void initialize() {
        for (InventoryDefinition definition : InventoryDefinition.values()) {
            if (isRegistered(definition)) {
                throw new IllegalStateException("Inventory is already registered: " + definition.name());
            }

            INVENTORIES.put(definition, definition.createInventory());
        }
    }

    public static Inventory getInventory(InventoryDefinition definition) {
        Inventory inventory = INVENTORIES.get(definition);

        if (inventory == null) {
            throw new IllegalStateException("Inventory is not registered: " + definition.name());
        }

        return inventory;
    }

    public static Optional<InventoryDefinition> findDefinition(Inventory inventory) {
        return InventoryDefinition
            .findDefinition(inventory)
            .filter(definition -> INVENTORIES.get(definition) == inventory);
    }

    public static boolean isRegistered(InventoryDefinition definition) {
        return INVENTORIES.containsKey(definition);
    }

    public static boolean isRegistered(Inventory inventory) {
        return findDefinition(inventory).isPresent();
    }
}
