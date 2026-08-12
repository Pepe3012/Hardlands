package org.heather.hardlands.common.player;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.heather.hardlands.Hardlands;

public final class PlayerUpdateTask extends BukkitRunnable {

    private PlayerUpdateTask() {}

    public static void initialize(Hardlands plugin, long period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive.");
        }

        new PlayerUpdateTask().runTaskTimer(plugin, 0L, period);
    }

    @Override
    public void run() {
        Hardlands.getInstance().getServer().getOnlinePlayers().forEach(this::updatePlayer);
    }

    private void updatePlayer(Player player) {
    }
}