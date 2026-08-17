package io.github.pepe3012.hardlands.module.world;

import io.github.pepe3012.hardlands.core.config.Configuration;
import io.github.pepe3012.hardlands.core.config.Option;
import io.github.pepe3012.hardlands.core.config.OptionValidators;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;
import org.popcraft.chunky.api.event.task.GenerationProgressEvent;

public final class WorldManager extends Configuration {

    public final Option<String> worldName = super.registerOption("world-name", String.class, OptionValidators.Strings.NON_BLANK);
    public final Option<Double> centerX = super.registerOption("center-x", Double.class);
    public final Option<Double> centerZ = super.registerOption("center-z", Double.class);
    public final Option<Integer> survivalBorderSize = super.registerOption("survival-border-size", Integer.class, OptionValidators.Integers.POSITIVE);
    public final Option<Integer> meetupBorderSize = super.registerOption("meetup-border-size", Integer.class, OptionValidators.Integers.POSITIVE);
    public final Option<Integer> deathmatchBorderSize = super.registerOption("deathmatch-border-size", Integer.class, OptionValidators.Integers.POSITIVE);
    public final Option<Integer> meetupShrinkDuration = super.registerOption("meetup-shrink-duration", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    public final Option<Integer> deathmatchShrinkDuration = super.registerOption("deathmatch-shrink-duration", Integer.class, OptionValidators.Integers.NON_NEGATIVE);
    public final Option<Boolean> teleport = super.registerOption("teleport", Boolean.class);
    public final Option<Boolean> damage = super.registerOption("damage", Boolean.class);

    private final ChunkyAPI chunky;

    private PregenerationState pregenerationState = PregenerationState.IDLE;
    private PregenerationRequest pregenerationRequest;
    private float pregenerationProgress;

    public WorldManager(ChunkyAPI chunky) {
        super("world");
        this.chunky = chunky;
        this.chunky.onGenerationProgress(this::handleGenerationProgress);
        this.chunky.onGenerationComplete(this::handleGenerationComplete);
    }

    public synchronized void startPregeneration() {
        if (this.isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is already running");
        }

        var request = this.pregenerationRequest != null
                ? this.pregenerationRequest
                : this.createPregenerationRequest();

        request.reviewAndAccept(this.chunky).ifPresent(accepted -> {
            this.pregenerationRequest = accepted;
            this.pregenerationState = PregenerationState.RUNNING;
        });
    }

    public synchronized void pausePregeneration() {
        if (!this.isPregenerationRunning()) {
            throw new IllegalStateException("Pregeneration is not running");
        }

        this.chunky.cancelTask(this.worldName.getValue());
        this.pregenerationState = PregenerationState.PAUSED;
    }

    public void shrinkSurvivalBorder() {
        var border = this.getWorld().getWorldBorder();

        border.setCenter(this.centerX.getValue(), this.centerZ.getValue());
        border.setSize(this.survivalBorderSize.getValue());
    }

    public void shrinkBorderForMeetup() {
        this.resizeBorder(this.meetupBorderSize.getValue(), this.meetupShrinkDuration.getValue());
    }

    public void shrinkBorderForDeathmatch() {
        this.resizeBorder(this.deathmatchBorderSize.getValue(), this.deathmatchShrinkDuration.getValue());
    }

    public PregenerationState getPregenerationState() {
        return this.pregenerationState;
    }

    public float getPregenerationProgress() {
        return this.pregenerationProgress;
    }

    public synchronized boolean isPregenerationRunning() {
        return this.pregenerationState == PregenerationState.RUNNING;
    }

    public synchronized boolean isPregenerationCompleted() {
        return this.pregenerationState == PregenerationState.COMPLETED;
    }

    private void resizeBorder(double size, long duration) {
        this.getWorld().getWorldBorder().changeSize(size, duration);
    }

    private synchronized void handleGenerationProgress(GenerationProgressEvent event) {
        this.pregenerationProgress = event.progress();
    }

    private synchronized void handleGenerationComplete(GenerationCompleteEvent event) {
        this.pregenerationProgress = 100.0F;
        this.pregenerationState = PregenerationState.COMPLETED;
    }

    private PregenerationRequest createPregenerationRequest() {
        return new PregenerationRequest(
                this.worldName.getValue(),
                this.centerX.getValue(),
                this.centerZ.getValue(),
                this.survivalBorderSize.getValue()
        );
    }

    private World getWorld() {
        var world = Bukkit.getWorld(this.worldName.getValue());

        if (world == null) {
            throw new IllegalStateException("World not found: " + this.worldName.getValue());
        }

        return world;
    }
}