package com.hardlands.world.chunky;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

import java.util.Objects;

public final class WorldPregenerationController {

    private final ChunkyAPI chunky;

    @Getter private volatile PregenerationState state = PregenerationState.IDLE;
    @Getter private volatile PregenerationRequest activeRequest;
    @Getter private volatile float progress;

    public WorldPregenerationController(ChunkyAPI chunky) {
        this.chunky = Objects.requireNonNull(chunky, "Chunky API cannot be null");

        chunky.onGenerationProgress(this::handleProgress);
        chunky.onGenerationComplete(event -> completePregeneration(event.world()));
    }

    public synchronized void startPregeneration(PregenerationRequest request) {
        Objects.requireNonNull(request, "Pregeneration request cannot be null");

        if (state != PregenerationState.IDLE) {
            throw new IllegalStateException("Pregeneration must be reset before starting again");
        }

        if (Bukkit.getWorld(request.worldName()) == null) {
            throw new IllegalArgumentException("World does not exist: " + request.worldName());
        }

        if (chunky.isRunning(request.worldName())) {
            throw new IllegalStateException("Chunky is already pregenerating " + request.worldName());
        }

        activeRequest = request;
        state = PregenerationState.RUNNING;
        progress = 0.0F;

        try {
            request.startPregeneration(chunky);
        } catch (RuntimeException exception) {
            resetState();
            throw exception;
        }
    }

    public synchronized void cancelPregeneration() {
        if (!isRunning()) {
            throw new IllegalStateException("Pregeneration is not running");
        }

        String worldName = activeRequest.worldName();

        if (chunky.isRunning(worldName) && !chunky.cancelTask(worldName)) {
            throw new IllegalStateException("Failed to cancel pregeneration for " + worldName);
        }

        resetState();
    }

    public synchronized void resetPregeneration() {
        if (isRunning()) {
            throw new IllegalStateException("Pregeneration cannot be reset while running");
        }

        resetState();
    }

    public boolean isRunning() {
        return state == PregenerationState.RUNNING;
    }

    public boolean isCompleted() {
        return state == PregenerationState.COMPLETED;
    }

    private synchronized void handleProgress(GenerationProgressEvent event) {
        if (!isActiveWorld(event.world())) return;

        if (Float.isFinite(event.progress())) {
            progress = Math.clamp(event.progress(), 0.0F, 100.0F);
        }

        if (event.complete()) {
            markCompleted();
        }
    }

    private synchronized void completePregeneration(String worldName) {
        if (!isActiveWorld(worldName)) return;

        markCompleted();
    }

    private boolean isActiveWorld(String worldName) {
        return state == PregenerationState.RUNNING && activeRequest != null && activeRequest.worldName().equals(worldName);
    }

    private void markCompleted() {
        progress = 100.0F;
        state = PregenerationState.COMPLETED;
    }

    private void resetState() {
        activeRequest = null;
        state = PregenerationState.IDLE;
        progress = 0.0F;
    }
}