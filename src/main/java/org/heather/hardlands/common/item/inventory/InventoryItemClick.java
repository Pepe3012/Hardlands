package org.heather.hardlands.common.item.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public record InventoryItemClick(
        ClickType type,
        Action action
) {

    public boolean matches(ClickType type) {
        return this.type == type;
    }

    public void execute(Inventory inventory, Player player) {
        this.action.execute(inventory, player);
    }

    @FunctionalInterface
    public interface Action {

        void execute(Inventory inventory, Player player);
    }
}