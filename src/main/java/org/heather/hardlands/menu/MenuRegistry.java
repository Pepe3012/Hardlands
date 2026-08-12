package org.heather.hardlands.menu;

import org.bukkit.inventory.Inventory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class MenuRegistry {

    private static final Map<MenuDefinition, Inventory> MENUS = new EnumMap<>(MenuDefinition.class);
    private static boolean frozen = false;

    private MenuRegistry() {}

    public static void register(MenuDefinition... definitions) {
        for (MenuDefinition definition : definitions) {
            MENUS.put(definition, definition.createInventory());
        }
    }

    public static void freeze() {
        frozen = true;
    }

    public static boolean isFreezed() {
        return frozen;
    }

    public static Inventory get(MenuDefinition menu) {
        return MENUS.get(menu);
    }

    public static boolean isManaged(Inventory inventory) {
        return MENUS.containsValue(inventory);
    }

    public static boolean isRegistered(MenuDefinition menu) {
        return MENUS.containsKey(menu);
    }

    public static Optional<MenuDefinition> findDefinition(Inventory inventory) {
        return MENUS.entrySet().stream()
                .filter(entry -> entry.getValue() == inventory)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}