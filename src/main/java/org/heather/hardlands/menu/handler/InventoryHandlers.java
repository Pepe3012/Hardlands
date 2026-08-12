package org.heather.hardlands.menu.handler;

import org.heather.hardlands.common.item.InventoryItem;

public final class InventoryHandlers {

    private InventoryHandlers() {}

    public static final InventoryHandler EMPTY = _ -> {};

    public static final InventoryHandler MAIN = editor -> {
        editor.putItem(grid -> grid.slot(4, 2), InventoryItem.GAME.build());
        editor.putItem(grid -> grid.slot(4, 3), InventoryItem.SCENARIOS.build());
        editor.putItem(grid -> grid.slot(4, 4), InventoryItem.PLAYERS.build());
        editor.putItem(grid -> grid.slot(4, 5), InventoryItem.WORLD.build());
        editor.putItem(grid -> grid.slot(4, 6), InventoryItem.SETTINGS.build());
        editor.putItem(grid -> grid.slot(4, 7), InventoryItem.TEMPLATES.build());
        editor.putItem(grid -> grid.slot(4, 8), InventoryItem.VANILLA_CHANGES.build());
    };
}