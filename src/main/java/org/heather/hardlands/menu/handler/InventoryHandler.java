package org.heather.hardlands.menu.handler;

import org.bukkit.entity.Player;

public interface InventoryHandler {

    void onCreate(InventoryEditor editor);

    default void onOpen(InventoryEditor editor, Player player) {}

    default void onClose(InventoryEditor editor, Player player) {}
}