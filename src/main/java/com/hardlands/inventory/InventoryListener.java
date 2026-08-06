package com.hardlands.inventory;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public final class InventoryListener implements Listener {

    private final Plugin plugin;

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        MenuInventory<?> inventory = getMenuInventory(topInventory);

        if (inventory == null) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player) || event.getClickedInventory() != topInventory) return;

        player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

        HardlandsMenu menu = (HardlandsMenu) inventory.getMenu();
        int slot = event.getRawSlot();

        Bukkit.getScheduler().runTask(this.plugin, () -> menu.handleClick(player, inventory, slot, event.getClick()));
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        if (getMenuInventory(event.getView().getTopInventory()) != null) event.setCancelled(true);
    }

    private static @Nullable MenuInventory<?> getMenuInventory(Inventory inventory) {
        if (!(inventory.getHolder() instanceof MenuInventory<?> holder)) return null;
        return holder.getMenu() instanceof HardlandsMenu ? holder : null;
    }
}