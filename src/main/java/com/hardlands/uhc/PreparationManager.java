package com.hardlands.uhc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class PreparationManager {

    private final WorldBorderManager worldBorderManager;
    private final ChunkyAPI chunky;

    @Getter private volatile PreparationState state = PreparationState.NOT_STARTED;
    @Getter private volatile String activeWorldName;
    @Getter private volatile float progress;

    public PreparationManager(WorldBorderManager worldBorderManager) {
        this.worldBorderManager = worldBorderManager;
        this.chunky = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (this.chunky == null) throw new IllegalStateException("Chunky is not installed or its API is unavailable");

        this.chunky.onGenerationProgress(this::handleProgress);
        this.chunky.onGenerationComplete(event -> this.complete(event.world()));
    }

    public void startPreparation() {
        if (this.isStarted()) throw new IllegalStateException("Preparation has already started");

        this.worldBorderManager.initializeForSurvival();

        WorldBorderManager.PregenerationRegion region = this.worldBorderManager.getPregenerationRegion();

        if (this.chunky.isRunning(region.worldName())) throw new IllegalStateException("Chunky is already generating the world " + region.worldName());

        this.activeWorldName = region.worldName();
        this.state = PreparationState.IN_PROGRESS;
        this.progress = 0.0F;

        if (!this.chunky.startTask(region.worldName(), "square", region.centerX(), region.centerZ(), region.radius(), region.radius(), "concentric")) {
            this.resetState();
            throw new IllegalStateException("Chunky failed to start pregenerating " + region.worldName());
        }
    }

    public void cancelPreparation() {
        if (!this.isInProgress()) throw new IllegalStateException("Preparation is not in progress");

        if (this.chunky.isRunning(this.activeWorldName) && !this.chunky.cancelTask(this.activeWorldName)) {
            throw new IllegalStateException("Failed to cancel pregeneration for " + this.activeWorldName);
        }

        this.resetState();
    }

    public void resetPreparation() {
        if (this.isInProgress()) throw new IllegalStateException("Preparation cannot be reset while pregeneration is running");

        this.resetState();
    }

    public boolean isStarted() {
        return this.state != PreparationState.NOT_STARTED;
    }

    public boolean isInProgress() {
        return this.state == PreparationState.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.state == PreparationState.COMPLETED;
    }

    private void handleProgress(GenerationProgressEvent event) {
        if (!this.isActiveWorld(event.world())) return;

        this.progress = Math.clamp(event.progress(), 0.0F, 100.0F);

        if (event.complete()) this.complete(event.world());
    }

    private void complete(String worldName) {
        if (!this.isActiveWorld(worldName)) return;

        this.progress = 100.0F;
        this.state = PreparationState.COMPLETED;
    }

    private boolean isActiveWorld(String worldName) {
        return this.isInProgress() && worldName.equals(this.activeWorldName);
    }

    private void resetState() {
        this.activeWorldName = null;
        this.state = PreparationState.NOT_STARTED;
        this.progress = 0.0F;
    }

    @Getter
    @RequiredArgsConstructor
    public enum PreparationState {

        NOT_STARTED("No iniciada"),
        IN_PROGRESS("Pregenerando"),
        COMPLETED("Lista");

        private final String displayName;
    }
}