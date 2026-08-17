package io.github.pepe3012.hardlands.listener;

import io.github.pepe3012.hardlands.common.item.InventoryItem;
import io.github.pepe3012.hardlands.module.inventory.InventoryRegistry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class InventoryListener implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        var topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isManaged(topInventory) || !(event.getWhoClicked() instanceof Player player)) return;

        var action = event.getAction();
        var clickedTop = event.getClickedInventory() == topInventory;
        var affectsTop = clickedTop 
                || action == InventoryAction.MOVE_TO_OTHER_INVENTORY 
                || action == InventoryAction.COLLECT_TO_CURSOR;

        if (!affectsTop) return;

        event.setCancelled(true);

        if (!clickedTop) return;

        InventoryItem.find(event.getCurrentItem()).ifPresent(item -> {
            if (!item.execute(topInventory, player, event.getClick())) return;
            player.playSound(player, Sound.UI_BUTTON_CLICK, 0.75F, 1.5F);
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        var topInventory = event.getView().getTopInventory();

        if (!InventoryRegistry.isManaged(topInventory)) return;

        boolean affectsTop = event.getRawSlots().stream()
                .anyMatch(slot -> slot >= 0 && slot < topInventory.getSize());
        if (affectsTop) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        var topInventory = event.getView().getTopInventory();
        InventoryRegistry.findDefinition(topInventory)
                .ifPresent(definition -> definition.handleClose(topInventory, player));
    }
}