package io.github.pepe3012.hardlands.common.task;

import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.pepe3012.hardlands.Hardlands;
import io.github.pepe3012.hardlands.common.item.inventory.InventoryItem;
import io.github.pepe3012.hardlands.config.inventory.InventoryRegistry;

public final class PlayerUpdateTask extends BukkitRunnable {

    private static final int PREPARATION_SLOT_OFFSET = 5;

    private PlayerUpdateTask() {}

    @Override
    public void run() {
        Hardlands.getInstance().getServer().getOnlinePlayers().forEach(player -> {

            Inventory inventory = player.getOpenInventory().getTopInventory();
            if (InventoryRegistry.isManaged(inventory)) {
                inventory.setItem(inventory.getSize() - PREPARATION_SLOT_OFFSET, InventoryItem.PREPARATION.build());
            }
        });
    }

    public static void initialize(final Hardlands plugin, long period) {
        new PlayerUpdateTask().runTaskTimer(plugin, 0L, period);
    }
}