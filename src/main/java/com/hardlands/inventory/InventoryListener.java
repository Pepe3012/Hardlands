package com.hardlands.inventory;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        MenuInventory menuInventory = getMenuInventory(topInventory);

        if (menuInventory == null || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        boolean clickedTop = event.getClickedInventory() == topInventory;
        boolean canReachTop = event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK;

        if (!clickedTop && !canReachTop) return;
        event.setCancelled(true);
        if (!clickedTop) return;

        BiConsumer<Player, ClickType> action = menuInventory.getAction(event.getRawSlot());
        if (action == null) return;

        player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.0F);
        action.accept(player, event.getClick());
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (getMenuInventory(topInventory) == null) return;

        int topSize = topInventory.getSize();
        if (event.getRawSlots().stream().anyMatch(slot -> slot < topSize)) {
            event.setCancelled(true);
        }
    }

    private static @Nullable MenuInventory getMenuInventory(Inventory inventory) {
        return inventory.getHolder() instanceof MenuInventory holder ? holder : null;
    }
}