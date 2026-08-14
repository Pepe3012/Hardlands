package io.github.pepe3012.hardlands.world.pregen;

import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class PregenerationController {

    private final ChunkyAPI chunky;

    private PregenerationState state = PregenerationState.IDLE;
    private PregenerationRequest activeRequest;
    private float progress;

    public PregenerationController(ChunkyAPI chunky) {
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::handleProgress);
        this.chunky.onGenerationComplete(this::handleCompletion);
    }

    public synchronized PregenerationState getState() {
        return this.state;
    }

    public synchronized PregenerationRequest getActiveRequest() {
        return this.activeRequest;
    }

    public synchronized float getProgress() {
        return this.progress;
    }

    public synchronized void startPregeneration(PregenerationRequest request) {
        if (this.state != PregenerationState.IDLE) {
            throw new IllegalStateException("Pregeneration must be reset before starting again");
        }

        String worldName = request.worldName();

        if (Bukkit.getWorld(worldName) == null) {
            throw new IllegalArgumentException("World does not exist: " + worldName);
        }

        if (this.chunky.isRunning(worldName)) {
            throw new IllegalStateException("Chunky is already pregenerating " + worldName);
        }

        this.activeRequest = request;
        this.state = PregenerationState.RUNNING;
        this.progress = 0.0F;

        try {
            request.reviewAndAccept(this.chunky);
        } catch (RuntimeException exception) {
            this.resetState();
            throw exception;
        }
    }

    public synchronized void cancelPregeneration() {
        if (!this.isRunning()) {
            throw new IllegalStateException("Pregeneration is not running");
        }

        String worldName = this.activeRequest.worldName();

        if (this.chunky.isRunning(worldName) && !this.chunky.cancelTask(worldName)) {
            throw new IllegalStateException("Failed to cancel pregeneration for " + worldName);
        }

        this.resetState();
    }

    public synchronized void resetPregeneration() {
        if (this.isRunning()) {
            throw new IllegalStateException("Pregeneration cannot be reset while running");
        }

        this.resetState();
    }

    public synchronized boolean isRunning() {
        return this.state == PregenerationState.RUNNING;
    }

    public synchronized boolean isCompleted() {
        return this.state == PregenerationState.COMPLETED;
    }

    private synchronized void handleProgress(GenerationProgressEvent event) {
        if (!this.isActiveWorld(event.world())) {
            return;
        }

        if (Float.isFinite(event.progress())) {
            this.progress = Math.clamp(event.progress(), 0.0F, 100.0F);
        }

        if (event.complete()) {
            this.complete();
        }
    }

    private synchronized void handleCompletion(GenerationCompleteEvent event) {
        if (this.isActiveWorld(event.world())) {
            this.complete();
        }
    }

    private boolean isActiveWorld(String worldName) {
        return this.state == PregenerationState.RUNNING
                && this.activeRequest != null
                && this.activeRequest.worldName().equals(worldName);
    }

    private void complete() {
        this.progress = 100.0F;
        this.state = PregenerationState.COMPLETED;
    }

    private void resetState() {
        this.activeRequest = null;
        this.state = PregenerationState.IDLE;
        this.progress = 0.0F;
    }
}