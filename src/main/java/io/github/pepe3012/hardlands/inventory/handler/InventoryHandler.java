package io.github.pepe3012.hardlands.inventory.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface InventoryHandler {

    InventoryHandler EMPTY = new InventoryHandler() {};

    default void onCreate(Inventory inventory) {}

    default void onOpen(Inventory inventory, Player player) {}

    default void onClose(Inventory inventory, Player player) {}
}