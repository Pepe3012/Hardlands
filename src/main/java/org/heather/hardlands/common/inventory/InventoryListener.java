package org.heather.hardlands.common.inventory;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.heather.hardlands.common.item.InventoryItem;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isRegistered(topInventory)
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        boolean clickedTop = event.getClickedInventory() == topInventory;

        if (!clickedTop && !affectsTopInventory(event.getAction())) {
            return;
        }

        event.setCancelled(true);

        if (!clickedTop) {
            return;
        }

        InventoryItem.findByItem(event.getCurrentItem())
                .ifPresent(item -> {
                    boolean handled = item.handleClick(event);

                    player.playSound(
                            player,
                            handled ? Sound.UI_BUTTON_CLICK : Sound.BLOCK_NOTE_BLOCK_BASS,
                            0.5F,
                            handled ? 1.5F : 0.5F);
                });
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isRegistered(topInventory)) {
            return;
        }

        boolean affectsTopInventory = event.getRawSlots().stream()
                .anyMatch(slot -> slot < topInventory.getSize());

        if (affectsTopInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getView().getTopInventory();

        InventoryRegistry.findDefinition(inventory)
                .ifPresent(definition -> definition.handleClose(inventory, player));
    }

    private static boolean affectsTopInventory(InventoryAction action) {
        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.COLLECT_TO_CURSOR;
    }
}