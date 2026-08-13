package org.heather.hardlands.config.inventory;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.heather.hardlands.common.item.inventory.InventoryItem;

public final class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isManaged(inventory)
                || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        boolean clickedTop = event.getClickedInventory() == inventory;
        boolean affectsTop = clickedTop
                || event.getClick().isShiftClick()
                || event.getClick() == ClickType.DOUBLE_CLICK;

        if (!affectsTop) return;

        event.setCancelled(true);

        if (!clickedTop) return;

        InventoryItem.find(event.getCurrentItem()).ifPresent(item -> {
            if (item.execute(inventory, player, event.getClick())) {
                player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.5F);
            }
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();

        if (InventoryRegistry.isManaged(inventory)
                && event.getRawSlots().stream().anyMatch(slot -> slot < inventory.getSize())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Inventory inventory = event.getView().getTopInventory();

        InventoryRegistry.findDefinition(inventory)
                .ifPresent(definition -> definition.handleClose(inventory, player));
    }
}