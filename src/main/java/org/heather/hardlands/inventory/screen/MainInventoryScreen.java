package org.heather.hardlands.inventory.screen;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.heather.hardlands.Hardlands;

public class MainInventoryScreen extends InventoryScreen {

    public MainInventoryScreen(final Hardlands plugin, Definition definition) {
        super(plugin, definition);
    }

    @Override
    protected void onInitialize(Inventory inventory, Player player) {

    }
}