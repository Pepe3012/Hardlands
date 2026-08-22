package org.heather.hardlands.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ThreadScheduler {
    private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
    private final Plugin plugin;

    public ThreadScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loop(Runnable task, long ticks) {
        this.thread.scheduleAtFixedRate(task, 0, milliseconds(ticks), TimeUnit.MILLISECONDS);
    }

    public void loopSync(Runnable task, long ticks) {
        this.loop(() -> Bukkit.getScheduler().runTask(this.plugin, task), ticks);
    }

    public void schedule(Runnable task, long ticks) {
        this.thread.schedule(task, milliseconds(ticks), TimeUnit.MILLISECONDS);
    }

    public void scheduleSync(Runnable task, long ticks) {
        this.schedule(() -> Bukkit.getScheduler().runTask(this.plugin, task), ticks);
    }

    public void cancel(ScheduledFuture<?> future) {
        future.cancel(true);
    }

    public void terminate() {
        this.thread.shutdownNow();
    }

    private static long milliseconds(long ticks) {
        return ticks * 50L;
    }
}
