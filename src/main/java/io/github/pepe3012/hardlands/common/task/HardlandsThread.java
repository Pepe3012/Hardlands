package io.github.pepe3012.hardlands.common.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class HardlandsThread {

    private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();

    public void loop(Runnable task, long ticks) {
        this.thread.scheduleAtFixedRate(task, 0, milliseconds(ticks), TimeUnit.MILLISECONDS);
    }

    public void schedule(Runnable task, long ticks) {
        this.thread.schedule(task, milliseconds(ticks), TimeUnit.MILLISECONDS);
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