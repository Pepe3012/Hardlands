package org.heather.hardlands.module.inventory.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface InventoryHandler {

    InventoryHandler EMPTY = new InventoryHandler() {};

    default void onCreate(Inventory inventory) {}

    default void onOpen(Inventory inventory, Player player) {}

    default void onClose(Inventory inventory, Player player) {}
}