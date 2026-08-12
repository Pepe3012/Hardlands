package org.heather.hardlands.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.heather.hardlands.common.item.InventoryItem;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!MenuRegistry.isManaged(topInventory) || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        boolean clickedTop = event.getClickedInventory() == topInventory;
        boolean affectsTop = clickedTop || event.getClick().isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK;

        if (!affectsTop) {
            return;
        }

        event.setCancelled(true);

        if (!clickedTop) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item != null) {
            InventoryItem.findAttachedMenu(item).ifPresent(definition -> {
                definition.openInventory(player);
            });
        }

        player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.25F);
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!MenuRegistry.isManaged(topInventory)) {
            return;
        }

        if (event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (!MenuRegistry.isManaged(inventory) || !(event.getPlayer() instanceof Player player)) {
            return;
        }

        MenuRegistry.findDefinition(inventory).ifPresent(definition -> {
            definition.handleClose(inventory, player);
        });
    }
}