package org.heather.hardlands.game;

import org.bukkit.scheduler.BukkitTask;
import org.heather.hardlands.Hardlands;

public final class GameTaskScheduler {

    private final Hardlands plugin;

    private BukkitTask task;

    public GameTaskScheduler(final Hardlands plugin) {
        this.plugin = plugin;
    }

    public void scheduleTask(Runnable action, long delay) {
        this.cancelScheduledTask();
        this.task = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.task = null;
            action.run();
        }, delay);
    }

    public void cancelScheduledTask() {
        if (this.task == null) return;

        this.task.cancel();
        this.task = null;
    }

    public boolean isTaskScheduled() {
        return this.task != null;
    }
}