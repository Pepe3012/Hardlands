package org.heather.hardlands.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.heather.hardlands.Hardlands;
import org.heather.hardlands.inventory.InventoryRegistry;

public final class GlobalUpdateTask extends BukkitRunnable {
    private GlobalUpdateTask() {}

    @Override
    public void run() {
    }

    public static void initialize(final Hardlands plugin, long period) {
        new GlobalUpdateTask().runTaskTimer(plugin, 0L, period);
    }
}
