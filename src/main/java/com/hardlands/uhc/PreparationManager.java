package com.hardlands.uhc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;

public final class PreparationManager {

    private final WorldBorderManager worldBorderManager;
    private final ChunkyAPI chunky;

    @Getter private volatile PreparationState state = PreparationState.NOT_STARTED;
    @Getter private volatile String activeWorldName;

    public PreparationManager(WorldBorderManager worldBorderManager) {
        this.worldBorderManager = worldBorderManager;
        this.chunky = Bukkit.getServicesManager().load(ChunkyAPI.class);

        if (this.chunky == null) throw new IllegalStateException("Chunky is not installed or its API is unavailable");

        this.chunky.onGenerationComplete(event -> this.handleGenerationComplete(event.world()));
    }

    public void startPreparation() {
        if (this.state != PreparationState.NOT_STARTED) {
            throw new IllegalStateException("Preparation has already started");
        }

        this.worldBorderManager.initializeForSurvival();

        WorldBorderManager.PregenerationRegion region = this.worldBorderManager.getPregenerationRegion();

        if (this.chunky.isRunning(region.worldName())) {
            throw new IllegalStateException("Chunky is already generating the world " + region.worldName());
        }

        this.activeWorldName = region.worldName();
        this.state = PreparationState.IN_PROGRESS;

        boolean started = this.chunky.startTask(region.worldName(), "square", region.centerX(), region.centerZ(), region.radius(), region.radius(), "concentric");
        if (!started) {
            this.resetState();
            throw new IllegalStateException("Chunky failed to start pregenerating " + region.worldName());
        }
    }

    public void cancelPreparation() {
        if (this.state != PreparationState.IN_PROGRESS) throw new IllegalStateException("Preparation is not in progress");

        if (this.activeWorldName != null && this.chunky.isRunning(this.activeWorldName) && !this.chunky.cancelTask(this.activeWorldName)) {
            throw new IllegalStateException("Failed to cancel pregeneration for " + this.activeWorldName);
        }

        this.resetState();
    }

    public void resetPreparation() {
        if (this.state == PreparationState.IN_PROGRESS) {
            throw new IllegalStateException("Preparation cannot be reset while pregeneration is running");
        }

        this.resetState();
    }

    public boolean isStarted() {
        return this.state != PreparationState.NOT_STARTED;
    }

    public boolean isCompleted() {
        return this.state == PreparationState.COMPLETED;
    }

    private void handleGenerationComplete(String worldName) {
        if (this.state != PreparationState.IN_PROGRESS) return;
        if (this.activeWorldName == null) return;
        if (!this.activeWorldName.equals(worldName)) return;

        this.state = PreparationState.COMPLETED;
    }

    private void resetState() {
        this.activeWorldName = null;
        this.state = PreparationState.NOT_STARTED;
    }

    @Getter
    @RequiredArgsConstructor
    public enum PreparationState {

        NOT_STARTED("Not Started"),
        IN_PROGRESS("Pregenerating"),
        COMPLETED("Completed");

        private final String displayName;
    }
}