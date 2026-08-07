package com.hardlands.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public final class MenuInventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        MenuInventory menu = getMenuInventory(topInventory);

        if (menu == null || !(event.getWhoClicked() instanceof Player player)) return;

        boolean clickedTop = event.getClickedInventory() == topInventory;
        boolean affectsTop = clickedTop || event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK;

        if (!affectsTop) return;

        event.setCancelled(true);
        if (!clickedTop) return;

        MenuAction action = menu.getAction(event.getRawSlot());
        if (action == null || !action.execute(player, event.getClick())) return;

        player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.0F);
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (getMenuInventory(topInventory) == null) return;

        if (event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize())) event.setCancelled(true);
    }

    private static @Nullable MenuInventory getMenuInventory(Inventory inventory) {
        return inventory.getHolder() instanceof MenuInventory menu ? menu : null;
    }
}