package org.heather.hardlands.menu.handler;

import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.menu.MenuDefinition;

import java.util.function.ToIntFunction;

@RequiredArgsConstructor
public final class InventoryEditor {

    private final Inventory inventory;
    private final MenuDefinition.Grid grid;

    public void putItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    public void putItem(ToIntFunction<MenuDefinition.Grid> slot, ItemStack item) {
        this.inventory.setItem(slot.applyAsInt(this.grid), item);
    }

    public void addItems(ItemStack... items) {
        this.inventory.addItem(items);
    }
}