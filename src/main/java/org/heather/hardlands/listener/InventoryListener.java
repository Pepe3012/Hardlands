package org.heather.hardlands.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.heather.hardlands.common.item.inventory.InventoryItem;
import org.heather.hardlands.module.inventory.InventoryRegistry;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isRegistered(topInventory)
                || !(event.getWhoClicked() instanceof Player player)) return;

        boolean clickedTop = event.getClickedInventory() == topInventory;

        if (!clickedTop && !affectsTopInventory(event.getAction())) return;

        event.setCancelled(true);

        if (!clickedTop) return;

        InventoryItem.find(event.getCurrentItem()).ifPresent(item -> {
            if (item.execute(topInventory, player, event.getClick())) {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.5F);
            }
        });
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isRegistered(topInventory)) return;

        boolean affectsTop = event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize());
        if (affectsTop) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Inventory inventory = event.getView().getTopInventory();
        InventoryRegistry.findDefinition(inventory)
                .ifPresent(definition -> definition.handleClose(inventory, player));
    }

    private static boolean affectsTopInventory(InventoryAction action) {
        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.COLLECT_TO_CURSOR;
    }
}