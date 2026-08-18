package org.heather.hardlands.module.world;

import org.bukkit.Material;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class PregenerationManager {

    private final ChunkyAPI chunky;

    private PregenerationRequest request;
    private State state = State.IDLE;
    private float progress;

    public PregenerationManager(ChunkyAPI chunky) {
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::handleGenerationProgress);
        this.chunky.onGenerationComplete(this::handleGenerationComplete);
    }

    private synchronized void handleGenerationProgress(GenerationProgressEvent event) {
        this.progress = event.progress();
    }

    private synchronized void handleGenerationComplete(GenerationCompleteEvent event) {
        this.progress = 100.0F;
        this.state = State.COMPLETED;
    }

    public synchronized void startPregeneration(PregenerationRequest request) {
        if (isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is already running");
        }

        request.reviewAndAccept(this.chunky).ifPresent(accepted -> {
            this.request = accepted;
            this.state = State.RUNNING;
        });
    }

    public synchronized void pausePregeneration() {
        if (!isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is not running");
        }

        this.chunky.cancelTask(this.request.worldName());
        this.state = State.PAUSED;
    }

    public synchronized State getPregenerationState() {
        return this.state;
    }

    public synchronized float getPregenerationProgress() {
        return this.progress;
    }

    public synchronized boolean isPregenerationRunning() {
        return this.state == State.RUNNING;
    }

    public synchronized boolean isPregenerationCompleted() {
        return this.state == State.COMPLETED;
    }

    public enum State {

        IDLE("idle", "<gray>Sin iniciar", Material.BEDROCK),
        RUNNING("running", "<yellow>En progreso", Material.DIRT),
        PAUSED("paused", "<gold>Pausado", Material.STONE),
        COMPLETED("completed", "<green>Completado", Material.GRASS_BLOCK);

        private final String key;
        private final String displayName;
        private final Material material;

        State(String key, String displayName, Material material) {
            this.key = key;
            this.displayName = displayName;
            this.material = material;
        }

        public String getKey() {
            return this.key;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}