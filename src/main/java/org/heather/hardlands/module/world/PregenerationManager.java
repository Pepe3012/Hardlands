package org.heather.hardlands.module.world;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class PregenerationManager {

    private final Map<String, PregenerationTask> pregenerating = new HashMap<>();
    private final ChunkyAPI chunky;

    public PregenerationManager(ChunkyAPI chunky) {
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::handleGenerationProgress);
        this.chunky.onGenerationComplete(this::handleGenerationComplete);
    }

    private synchronized void handleGenerationProgress(GenerationProgressEvent event) {
        PregenerationTask task = this.pregenerating.get(event.world());

        if (task == null) return;

        this.pregenerating.put(
                event.world(),
                event.progress() >= 100
                        ? task.withCompletedState()
                        : task.withProgress(event.progress()));
    }

    private synchronized void handleGenerationComplete(GenerationCompleteEvent event) {
        PregenerationTask task = this.pregenerating.get(event.world());

        if (task == null) return;

        this.pregenerating.put(event.world(), task.withCompletedState());
    }

    public synchronized void reviewAndAccept(PregenerationRequest request) {
        String worldName = request.worldName();

        if (!request.review(this.chunky)) {
            throw new IllegalStateException("Chunky is already pregenerating world: " + worldName);
        }

        this.pregenerating.put(worldName, PregenerationTask.withRunningState());
    }

    public synchronized void pause() {
        this.pregenerating.replaceAll(
                (worldName, task) -> {
                    if (!this.chunky.pauseTask(worldName)) {
                        throw new IllegalStateException(
                                "Unable to pause pregeneration for world: " + worldName);
                    }

                    return task.withPausedState();
                });
    }

    public synchronized State getState() {
        if (this.pregenerating.isEmpty()) return State.IDLE;

        for (PregenerationTask task : this.pregenerating.values()) {
            State state = task.state();

            if (state == State.RUNNING) return State.RUNNING;
            if (state == State.PAUSED) return State.PAUSED;
        }

        return State.COMPLETED;
    }

    public synchronized float getProgress() {
        if (this.pregenerating.isEmpty()) return 0.0F;

        float totalProgress = 0.0F;

        for (PregenerationTask task : this.pregenerating.values()) {
            totalProgress += task.progress();
        }

        return totalProgress / this.pregenerating.size();
    }

    private record PregenerationTask(State state, float progress) {

        private PregenerationTask withProgress(float progress) {
            return new PregenerationTask(this.state, progress);
        }

        private PregenerationTask withPausedState() {
            return new PregenerationTask(State.PAUSED, this.progress);
        }

        private PregenerationTask withCompletedState() {
            return new PregenerationTask(State.COMPLETED, 100.0F);
        }

        private static PregenerationTask withRunningState() {
            return new PregenerationTask(State.RUNNING, 0.0F);
        }
    }

    public enum State {
        IDLE("idle", "Sin iniciar", Material.BEDROCK, NamedTextColor.GRAY),
        RUNNING("running", "En progreso", Material.DIRT, NamedTextColor.YELLOW),
        PAUSED("paused", "Pausado", Material.STONE, NamedTextColor.GOLD),
        COMPLETED("completed", "Completado", Material.GRASS_BLOCK, NamedTextColor.GREEN);

        private final String key;
        private final String name;
        private final Material material;
        private final TextColor textColor;

        State(String key, String name, Material material, TextColor textColor) {
            this.key = key;
            this.name = name;
            this.material = material;
            this.textColor = textColor;
        }

        public String getKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        public Material getMaterial() {
            return this.material;
        }

        public TextColor getTextColor() {
            return this.textColor;
        }

        public Component getDisplay() {
            return Component.text(this.name, this.textColor);
        }
    }
}
