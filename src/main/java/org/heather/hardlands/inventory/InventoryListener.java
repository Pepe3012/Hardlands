package org.heather.hardlands.inventory;

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
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isManaged(topInventory) || !(event.getWhoClicked() instanceof Player player)) return;

        boolean clickedTop = event.getClickedInventory() == topInventory;
        boolean affectsTop = clickedTop
                || event.getClick().isShiftClick()
                || event.getClick() == ClickType.DOUBLE_CLICK;

        if (!affectsTop) return;

        event.setCancelled(true);
        if (!clickedTop) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        InventoryItem.findAttachedMenu(item).ifPresent(definition -> {
            definition.openInventory(player);
            player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.25F);
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (InventoryRegistry.isManaged(topInventory)
                && event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        Inventory topInventory = event.getView().getTopInventory();

        InventoryRegistry.findDefinition(topInventory)
                .ifPresent(definition -> definition.handleClose(topInventory, player));
    }
}