package org.heather.hardlands.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.heather.hardlands.Hardlands;

public final class PlayerUpdateTask extends BukkitRunnable {
    private PlayerUpdateTask() {}

    @Override
    public void run() {
        Hardlands.getInstance().getServer().getOnlinePlayers().forEach(player -> {

        });
    }

    public static void initialize(final Hardlands plugin, long period) {
        new PlayerUpdateTask().runTaskTimer(plugin, 0L, period);
    }
}