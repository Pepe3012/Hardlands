package io.github.pepe3012.hardlands.game;

import io.github.pepe3012.hardlands.Hardlands;
import org.bukkit.scheduler.BukkitTask;

public final class GameTaskScheduler {

    private final Hardlands plugin;
    private BukkitTask scheduledTask;

    public GameTaskScheduler(Hardlands plugin) {
        this.plugin = plugin;
    }

    public void scheduleTask(Runnable action, long delay) {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }

        if (delay < 0L) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }

        this.cancelScheduledTask();

        this.scheduledTask = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            this.scheduledTask = null;
            action.run();
        }, delay);
    }

    public void cancelScheduledTask() {
        if (this.scheduledTask == null) {
            return;
        }

        this.scheduledTask.cancel();
        this.scheduledTask = null;
    }

    public boolean isTaskScheduled() {
        return this.scheduledTask != null && !this.scheduledTask.isCancelled();
    }
}