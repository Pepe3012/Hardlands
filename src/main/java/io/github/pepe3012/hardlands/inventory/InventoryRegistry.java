package io.github.pepe3012.hardlands.inventory;

import org.bukkit.inventory.Inventory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class InventoryRegistry {

    private static final Map<InventoryDefinition, Inventory> INVENTORIES = new EnumMap<>(InventoryDefinition.class);

    private InventoryRegistry() {}

    public static void register(InventoryDefinition... definitions) {
        for (InventoryDefinition definition : definitions) {
            if (INVENTORIES.containsKey(definition)) {
                throw new IllegalStateException("Inventory is already registered: " + definition.name());
            }

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
        return InventoryDefinition.find(inventory).filter(definition -> INVENTORIES.get(definition) == inventory);
    }

    public static boolean isRegistered(InventoryDefinition definition) {
        return INVENTORIES.containsKey(definition);
    }

    public static boolean isManaged(Inventory inventory) {
        return findDefinition(inventory).isPresent();
    }
}