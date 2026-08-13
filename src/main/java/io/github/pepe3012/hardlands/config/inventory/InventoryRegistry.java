package io.github.pepe3012.hardlands.config.inventory;

import org.bukkit.inventory.Inventory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class InventoryRegistry {

    private static final Map<InventoryDefinition, Inventory> INVENTORIES = new EnumMap<>(InventoryDefinition.class);

    private InventoryRegistry() {}

    public static void register(InventoryDefinition... definitions) {
        for (InventoryDefinition definition : definitions) {
            INVENTORIES.put(definition, definition.createInventory());
        }
    }

    public static Inventory get(InventoryDefinition definition) {
        Inventory inventory = INVENTORIES.get(definition);

        if (inventory == null) {
            throw new IllegalStateException("Inventory is not registered: " + definition.name());
        }

        return inventory;
    }

    public static Optional<InventoryDefinition> findDefinition(Inventory inventory) {
        return INVENTORIES.entrySet().stream()
                .filter(entry -> entry.getValue() == inventory)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public static boolean isRegistered(InventoryDefinition definition) {
        return INVENTORIES.containsKey(definition);
    }

    public static boolean isManaged(Inventory inventory) {
        return INVENTORIES.values().stream().anyMatch(registered -> registered == inventory);
    }
}